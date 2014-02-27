(ns aituhaku-e2e.util
  (:require [aitu-e2e.util :refer [avaa]]))

(defn aituhaku-url [polku]
  (str (or (System/getenv "AITUHAKU_URL")
           "http://localhost:8081")
       polku))

(defn avaa-aituhaku
  [polku]
  (aitu-e2e.util/avaa aituhaku-url polku))
