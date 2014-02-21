(ns aituhaku.palvelin
  (:gen-class)
  (:require [clojure.tools.logging :as log]
            [compojure.core :as c]
            [org.httpkit.server :as hs]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.util.response :as resp]
            schema.core
            [aitu.infra.print-wrapper :refer [log-request-wrapper]]
            [aituhaku.asetukset :refer [lue-asetukset oletusasetukset konfiguroi-lokitus]]
            aituhaku.rest-api.tutkinto))

(schema.core/set-fn-validation! true)

(defn ^:private reitit [asetukset]
  (c/routes
    (c/context "/api/tutkinto" [] aituhaku.rest-api.tutkinto/reitit)
    (c/GET "/" [] (resp/redirect "index.html"))))

(defn sammuta [palvelin]
  ((:sammuta palvelin)))

(defn kaynnista! [oletusasetukset]
  (try
    (let [asetukset (lue-asetukset oletusasetukset)
          _ (konfiguroi-lokitus asetukset)
          sammuta (hs/run-server (->
                                   (reitit asetukset)
                                   wrap-keyword-params
                                   wrap-json-params
                                   wrap-params
                                   (wrap-resource "public/app")
                                   wrap-content-type
                                   log-request-wrapper)
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
