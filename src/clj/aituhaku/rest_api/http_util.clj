(ns aituhaku.rest-api.http-util
  (:require
    [cheshire.core :as cheshire]
    [schema.core :as s]
    [aituhaku.asetukset :refer [asetukset]]
    [oph.common.util.http-util :refer [response-or-404]]))

(defn response-nocache
  [data]
  (assoc-in (response-or-404 data) [:headers "Cache-control"] "max-age=0"))