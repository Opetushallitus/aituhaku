(ns aituhaku.rest-api.opintoala
  (:require [compojure.core :as c]
            [schema.core :as schema]
            [aituhaku.rest-api.http-util :refer [json-response]]
            [aituhaku.toimiala.skeema :refer [Koulutusala]]
            [aituhaku.arkisto.opintoala :as arkisto]))

(c/defroutes reitit
  (c/GET "/haku" []
    (json-response
      (arkisto/hae)
      [Koulutusala])))
