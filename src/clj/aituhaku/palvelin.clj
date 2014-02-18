(ns aituhaku.palvelin
  (:gen-class)
  (:require [clojure.tools.logging :as log]
            [compojure.core :as c]
            [org.httpkit.server :as hs]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.util.response :as resp]
            [aituhaku.asetukset :refer [lue-asetukset oletusasetukset konfiguroi-lokitus]]))

(defn ^:private reitit [asetukset]
  (c/routes
    (c/GET "/" [] (resp/redirect "index.html"))))

(defn sammuta [palvelin]
  ((:sammuta palvelin)))

(defn kaynnista! [oletusasetukset]
  (try
    (let [asetukset (lue-asetukset oletusasetukset)
          _ (konfiguroi-lokitus asetukset)
          sammuta (hs/run-server (->
                                   (reitit asetukset)
                                   (wrap-resource "public/app")
                                   (wrap-content-type))
                                 {:port (-> asetukset :server :port Integer/parseInt)})]
      {:sammuta sammuta})
    (catch Throwable t
      (let [virheviesti "Palvelimen käynnistys epäonnistui"]
        (log/error t virheviesti)
        (binding [*out* *err*]
          (println virheviesti))
        (.printStackTrace t *err*)
        (System/exit 1)))))

(defn -main []
  (kaynnista! oletusasetukset))
