(ns aituhaku.palvelin
  (:gen-class)
  (:require [org.httpkit.server :as hs]
            [aituhaku.asetukset :refer [oletusasetukset]]
            [ring.middleware.resource :refer [wrap-resource]]
            [compojure.core :as c]
            [stencil.core :as s]
            [stencil.loader :as sl]
            [clojure.tools.logging :as log]

            [aituhaku.asetukset :refer [oletusasetukset konfiguroi-lokitus]]))

(defn ^:private reitit []
  (c/routes
    (c/GET "/" []
      (s/render-file "template/index" {}))))

(defn sammuta [palvelin]
  ((:sammuta palvelin)))

(defn kaynnista! [asetukset]
  (try
    (let [_ (konfiguroi-lokitus asetukset)
          sammuta (hs/run-server (->
                                   (reitit)
                                   (wrap-resource "public"))
                                 {:port (-> asetukset :server :port)})]
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
