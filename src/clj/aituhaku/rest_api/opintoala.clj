(ns aituhaku.rest-api.opintoala
  (:require [compojure.core :as c]
            [schema.core :as schema]
            [aitu.rest-api.http-util :refer [cachable-json-response json-response]]
            [aituhaku.toimiala.skeema :refer [Opintoala]]
            [aituhaku.arkisto.opintoala :as arkisto]))

(c/defroutes reitit
  (c/GET "/haku" [termi :as req]
    (schema/validate schema/Str termi)
      (cachable-json-response req
        (arkisto/hae-termilla termi)
        [Opintoala])))
