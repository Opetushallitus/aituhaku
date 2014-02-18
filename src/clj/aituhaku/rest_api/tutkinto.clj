(ns aituhaku.rest-api.tutkinto
  (:require [compojure.core :as c]
            [aitu.rest-api.http-util :refer [cachable-json-response json-response]]))

(c/defroutes reitit
  (c/GET "/haku" [termi :as req]
    (cachable-json-response req [{:nimi "Tutkinto 1"}
                                 {:nimi "Tutkinto 2"}])))
