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

(ns aituhaku-e2e.util
  (:require [aitu-e2e.util :refer [avaa]]))

(defn aituhaku-url [polku]
  (str (or (System/getenv "AITUHAKU_URL")
           "http://localhost:8081")
       polku))

(defn avaa-aituhaku
  [polku]
  (aitu-e2e.util/avaa aituhaku-url polku))
