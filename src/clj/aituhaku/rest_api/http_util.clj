(ns aituhaku.rest-api.http-util
  (:require
    [cheshire.core :as cheshire]
    [schema.core :as s]
    [aituhaku.asetukset :refer [asetukset]]))


(defn json-response
  ([data]
   (if (nil? data)
     {:status 404}
     {:status 200
      :body (cheshire/generate-string data)
      :headers {"Content-Type" "application/json"
                "Cache-control" (str "max-age=" (:response-cache-max-age @asetukset))}}))
  ([data schema]
   (json-response (s/validate (s/maybe schema) data))))
