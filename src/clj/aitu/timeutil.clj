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

(ns aitu.timeutil
  (:require [clojure.core.typed :as t]
            [clj-time.core :as ctime]
            [clj-time.coerce :as tcoerce])
  (:import org.joda.time.LocalDate))

(t/ann ^:no-check clj-time.core/before? [LocalDate LocalDate -> Boolean])
(t/ann ^:no-check clj-time.core/after? [LocalDate LocalDate -> Boolean])
(t/ann ^:no-check clj-time.core/today [-> LocalDate])
(t/ann ^:no-check clj-time.core/local-date [t/AnyInteger t/AnyInteger t/AnyInteger -> LocalDate])

; työn alla..
; mutta tarkoitus on kerätä tänne kamaa, jolla voidaan käsitellä versiointia ja voimassaoloa järkevästi

(t/ann time-forever LocalDate)
(def time-forever (ctime/local-date 2199 1 1))

(t/ann pvm-menneisyydessa? [LocalDate -> Boolean])
(defn pvm-menneisyydessa? [pvm]
  (let [nytpvm (ctime/today)]
    (ctime/after? nytpvm pvm)))

(t/ann pvm-tulevaisuudessa? [LocalDate -> Boolean])
(defn pvm-tulevaisuudessa? [pvm]
  (let [nytpvm (ctime/today)]
    (ctime/before? nytpvm pvm)))

(def pvm-mennyt-tai-tanaan?
  (complement pvm-tulevaisuudessa?))

(def pvm-tuleva-tai-tanaan?
  (complement pvm-menneisyydessa?))
