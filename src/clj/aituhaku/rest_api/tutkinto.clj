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

(ns aituhaku.rest-api.tutkinto
  (:require [compojure.core :as c]
            [schema.core :as schema]
            [aituhaku.rest-api.http-util :refer [json-response]]
            [aituhaku.toimiala.skeema :refer [Tutkinto TutkintoTiedot]]
            [aituhaku.arkisto.tutkinto :as arkisto]))

(c/defroutes reitit
  (c/GET "/haku" [nimi opintoala :as req]
    (schema/validate (schema/maybe schema/Str) nimi)
    (schema/validate (schema/maybe schema/Str) opintoala)
    (json-response
      (arkisto/hae-ehdoilla nimi opintoala)
      [Tutkinto]))

  (c/GET "/:tutkintotunnus" [tutkintotunnus :as req]
    (schema/validate schema/Str tutkintotunnus)
    (json-response
      (arkisto/hae tutkintotunnus)
      TutkintoTiedot)))
