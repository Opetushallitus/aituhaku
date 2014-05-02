(ns aituhaku.rest-api.opintoala
  (:require [compojure.core :as c]
            [schema.core :as schema]
            [aituhaku.rest-api.http-util :refer [json-response]]
            [aituhaku.toimiala.skeema :refer [Opintoala]]
            [aituhaku.arkisto.opintoala :as arkisto]))

(c/defroutes reitit
  (c/GET "/haku" [termi kieli]
    (schema/validate schema/Str termi)
    (json-response
      (arkisto/hae-termilla termi kieli)
      [Opintoala])))
