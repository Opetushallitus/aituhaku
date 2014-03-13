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
  (:require [aituhaku.arkisto.sql.tutkinto :as tutkinto-sql]
            [aitu.util :refer [sisaltaako-kentat?]]
            [aitu.timeutil :as timeutil]))

(defn tutkinto-voimassa?
  [tutkinto]
  (and (timeutil/pvm-mennyt-tai-tanaan? (:voimassa_alkupvm tutkinto))
       (timeutil/pvm-tuleva-tai-tanaan? (:siirtymaajan_loppupvm tutkinto))))

(defn hakuehto-predikaatti
  [hakuehdot]
  (fn[x]
    (every?
      #(if (:hakuehto %)
         (sisaltaako-kentat? x (:kentat %) (:hakuehto %))
         true)
      hakuehdot)))

(defn hae-ehdoilla
  "Hakee kentistä ehdoilla."
  [hakuehdot]
  (->> (tutkinto-sql/hae-tutkintojen-tiedot)
    (filter tutkinto-voimassa?)
    (filter (hakuehto-predikaatti hakuehdot))))

(defn hae
  [tutkintotunnus]
  (first (tutkinto-sql/hae tutkintotunnus)))

