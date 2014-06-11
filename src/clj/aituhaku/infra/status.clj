;; Copyright (c) 2014 The Finnish National Board of Education - Opetushallitus
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

(ns aituhaku.infra.status
  (:require [clojure.java.io :refer [resource]]))

(defn piilota-salasanat [status]
  (clojure.walk/postwalk #(if (and (vector? %)
                                   (= :password (first %)))
                            [:password "*****"]
                            %)
                         status))

(defn status []
  {:build-id (if-let [r (resource "build-id.txt")]
               (.trim (slurp r))
               "dev")
   :asennukset (try
                 (slurp "asennukset.txt")
                 (catch java.io.FileNotFoundException _
                   nil))})