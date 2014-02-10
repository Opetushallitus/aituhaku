(ns aituhaku.palvelin
  (:gen-class)
  (:require [org.httpkit.server :as hs]
            [aituhaku.asetukset :refer [oletusasetukset]]))

(defn server [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello world!"})

(defn sammuta [palvelin]
  ((:sammuta palvelin)))

(defn kaynnista! [asetukset]
  {:sammuta (hs/run-server server {:port (-> asetukset :server :port Integer/parseInt)})})

(defn -main []
  (kaynnista! oletusasetukset))
