;; Copyright (c) 2013 The Finnish National Board of Education - Opetushallitus
;;
;; This program is free software:  Licensed under the EUPL, Version 1.1 or - as
;; soon as they will be approved by the European Commission - subsequent versions
;; of the EUPL (the "Licence");
;;
;; You may not use this work except in compliance with the Licence.
;; You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
;;
;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; European Union Public Licence for more details.

(ns aituhaku.arkisto.tutkinto
  (:require [clojure.core.typed :as t]
            [aituhaku.arkisto.sql.tutkinto :as tutkinto-sql
             :refer [TutkinnonPerustiedot Tutkinto]]
            [aitu.util :refer [sisaltaako-kentat?]]
            [aitu.timeutil :as timeutil]))

(t/ann tutkinto-voimassa? [TutkinnonPerustiedot -> Boolean])
(defn tutkinto-voimassa?
  [tutkinto]
  (and (timeutil/pvm-mennyt-tai-tanaan? (:voimassa_alkupvm tutkinto))
       (timeutil/pvm-tuleva-tai-tanaan? (:siirtymaajan_loppupvm tutkinto))))

(t/ann hae-ehdoilla [String String -> (t/Seq TutkinnonPerustiedot)])
(defn hae-ehdoilla
  "Hakee kentistä ehdoilla."
  [nimi opintoala]
  (->> (tutkinto-sql/hae-tutkintojen-tiedot opintoala)
    (filter tutkinto-voimassa?)
    (filter (t/ann-form #(sisaltaako-kentat? % [:nimi_fi :nimi_sv] nimi)
                        [TutkinnonPerustiedot -> Boolean]))))

(t/ann hae [String -> (t/Option Tutkinto)])
(defn hae
  [tutkintotunnus]
  (first (tutkinto-sql/hae tutkintotunnus)))
