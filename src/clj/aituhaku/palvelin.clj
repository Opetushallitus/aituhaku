;; Copyright (c) 2013 The Finnish National Board of Education - Opetushallitus
;;
;; This program is free software:  Licensed under the EUPL, Version 1.1 or - as
;; soon as they will be approved by the European Commission - subsequent versions
;; of the EUPL (the "Licence");
;;
;; You may not use this work except in compliance with the Licence.
;; You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
;;
;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; European Union Public Licence for more details.

(ns aituhaku.palvelin
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [clojure.pprint :refer [pprint]]
            [compojure.core :as c]
            [compojure.route :as r]
            [org.httpkit.server :as hs]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [ring.util.response :as resp]
            [cheshire.generate :as json-gen]
            schema.core
            [aitu.infra.print-wrapper :refer [log-request-wrapper]]
            [aituhaku.asetukset :refer [asetukset lue-asetukset oletusasetukset konfiguroi-lokitus]]
            [aituhaku.infra.i18n :refer [wrap-locale]]
            [aituhaku.infra.status :refer [status piilota-salasanat build-id]]
            [stencil.core :as s]
            aituhaku.rest-api.i18n
            aituhaku.rest-api.js-log
            aituhaku.rest-api.jarjestaja
            aituhaku.rest-api.opintoala
            aituhaku.rest-api.tutkinto
            aituhaku.rest-api.toimikunta))

(schema.core/set-fn-validation! true)

(defn ^:private render-index [asetukset request]
  (let [fi (aituhaku.rest-api.i18n/hae-tekstit "fi")
        sv (aituhaku.rest-api.i18n/hae-tekstit "sv")
        server-name (:server-name request)
        tekstit (if
                  (some #(= server-name %) ["www.sokexamen.fi" "www.xn--skexamen-n4a.fi" "sokexamen.fi" "xn--skexamen-n4a.fi"])
                  sv
                  fi)]
    (s/render-file "public/app/index.html" {:base-url (-> asetukset :server :base-url)
                                            :description (get-in tekstit [:meta :description])
                                            :keywords (get-in tekstit [:meta :keywords])})))

(defn ^:private reitit [asetukset]
  (c/routes
    (c/context "/api/tutkinto" [] aituhaku.rest-api.tutkinto/reitit)
    (c/context "/api/toimikunta" [] aituhaku.rest-api.toimikunta/reitit)
    (c/context "/api/i18n" [] aituhaku.rest-api.i18n/reitit)
    (c/context "/api/opintoala" [] aituhaku.rest-api.opintoala/reitit)
    (c/context "/api/jarjestaja" [] aituhaku.rest-api.jarjestaja/reitit)
    (c/context "/api/jslog" []  aituhaku.rest-api.js-log/reitit )
    (if (:development-mode asetukset)
      (c/GET "/status" [] (s/render-file "status" (assoc (status)
                                                         :asetukset (with-out-str
                                                                      (-> asetukset
                                                                        piilota-salasanat
                                                                        pprint)))))
     (c/GET "/status" [] (s/render-string "OK" {})))
    (c/GET "/" request (render-index asetukset request))
    (r/not-found "Not found")))

(defn sammuta [palvelin]
  (log/info "Sammutetaan näyttötutkintohaku")
  ((:sammuta palvelin))
  (log/info "Palvelin sammutettu"))

(defn wrap-expires [handler]
  (fn [request]
    (assoc-in (handler request) [:headers "Expires"] "-1")))

(defn app [asetukset]
  (json-gen/add-encoder org.joda.time.LocalDate
    (fn [c json-generator]
      (.writeString json-generator (.toString c "yyyy-MM-dd"))))
  (->
    (reitit asetukset)
    wrap-keyword-params
    wrap-json-params
    (wrap-resource "public/app")
    (wrap-locale
      :ei-redirectia #"/api/.*"
      :base-url (get-in asetukset [:server :base-url]))
    wrap-params
    wrap-content-type
    wrap-not-modified
    wrap-expires
    log-request-wrapper))

(defn kaynnista! [oletusasetukset]
  (try
    (log/info "Käynnistetään näyttötutkintohaku, versio" @build-id)
    (let [luetut-asetukset (lue-asetukset oletusasetukset)
          _ (deliver asetukset luetut-asetukset )
          _ (konfiguroi-lokitus luetut-asetukset)
          _ (aituhaku.arkisto.sql.korma/luo-db (:db luetut-asetukset))
          portti (get-in luetut-asetukset [:server :port])
          sammuta (hs/run-server (app luetut-asetukset)
                    {:port portti})
          _ (log/info "Palvelin käynnistetty porttiin " portti)]
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
