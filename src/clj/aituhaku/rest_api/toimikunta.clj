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

(ns aituhaku.rest-api.toimikunta
  (:require [compojure.api.core :refer [defroutes GET]]
            [schema.core :as s]
            [aituhaku.arkisto.sql.toimikunta :as arkisto]
            [aituhaku.toimiala.skeema :refer [Toimikunta Toimikunta-plain]]
            [oph.common.util.http-util :refer [response-or-404]]))

(defroutes reitit
  (GET "/:tkunta" []
    :path-params [tkunta :- s/Str]
    :return Toimikunta
    (response-or-404 (arkisto/hae tkunta)))
  
  (GET "/" []
     :return [Toimikunta-plain] 
     (response-or-404 (arkisto/hae-kaikki))))
