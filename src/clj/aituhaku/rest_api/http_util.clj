(ns aituhaku.rest-api.http-util
  (:require
    [cheshire.core :as cheshire]
    [schema.core :as s]
    [aituhaku.asetukset :refer [asetukset]]
    [oph.common.util.http-util :refer [response-or-404]]))

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

(defn json-response-nocache
  [data]
  (assoc-in (json-response data) [:headers "Cache-control"] "max-age=0"))

(defn response-nocache
  [data]
  (assoc-in (response-or-404 data) [:headers "Cache-control"] "max-age=0"))