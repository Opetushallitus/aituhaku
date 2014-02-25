(ns aituhaku.rest-api.toimikunta
  (:require [compojure.core :as c]
            [aitu.rest-api.http-util :refer [json-response]]
            [aituhaku.toimiala.skeema :refer [Toimikunta]]
            [aituhaku.arkisto.sql.toimikunta :as arkisto]))

(c/defroutes reitit
  (c/GET "/:tkunta" [tkunta :as req]
    (json-response (arkisto/hae tkunta) Toimikunta)))
