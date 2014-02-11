(ns aituhaku.palvelin
  (:gen-class)
  (:require [org.httpkit.server :as hs]
            [aituhaku.asetukset :refer [oletusasetukset]]
            [ring.middleware.resource :refer [wrap-resource]]
            [compojure.core :as c]
            [stencil.core :as s]
            [stencil.loader :as sl]))

(defn ^:private reitit []
  (c/routes
    (c/GET "/" []
      (s/render-file "template/index" {}))))

(defn sammuta [palvelin]
  ((:sammuta palvelin)))

(defn kaynnista! [asetukset]
  {:sammuta (hs/run-server (->
                             (reitit)
                             (wrap-resource "public"))
              {:port (-> asetukset :server :port Integer/parseInt)})})

(defn -main []
  (kaynnista! oletusasetukset))
