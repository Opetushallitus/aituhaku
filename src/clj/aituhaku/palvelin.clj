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
  (:require [clojure.tools.logging :as log]
            [compojure.core :as c]
            [org.httpkit.server :as hs]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.util.response :as resp]
            [cheshire.generate :as json-gen]
            schema.core
            [aitu.infra.print-wrapper :refer [log-request-wrapper]]
            [aituhaku.asetukset :refer [lue-asetukset oletusasetukset konfiguroi-lokitus]]
            [aituhaku.infra.i18n :refer [wrap-locale]]
            [stencil.core :as s]
            aituhaku.rest-api.tutkinto
            aituhaku.rest-api.toimikunta
            aituhaku.rest-api.i18n))

(schema.core/set-fn-validation! true)

(defn ^:private reitit [asetukset]
  (c/routes
    (c/context "/api/tutkinto" [] aituhaku.rest-api.tutkinto/reitit)
    (c/context "/api/toimikunta" [] aituhaku.rest-api.toimikunta/reitit)
    (c/context "/api/i18n" [] aituhaku.rest-api.i18n/reitit)
    (c/GET "/" [] (s/render-file "public/app/index.html" {:base-url (-> asetukset :server :base-url)}))))

(defn sammuta [palvelin]
  ((:sammuta palvelin)))

(defn kaynnista! [oletusasetukset]
  (try
    (let [asetukset (lue-asetukset oletusasetukset)
          _ (konfiguroi-lokitus asetukset)
          _ (aituhaku.arkisto.sql.korma/luo-db (:db asetukset))
          _ (json-gen/add-encoder org.joda.time.LocalDate
              (fn [c json-generator]
                (.writeString json-generator (.toString c "yyyy-MM-dd"))))
          sammuta (hs/run-server (->
                                   (reitit asetukset)
                                   wrap-keyword-params
                                   wrap-json-params
                                   (wrap-resource "public/app")
                                   (wrap-locale
                                     :ei-redirectia #"/api/.*"
                                     :base-url (-> asetukset :server :base-url))
                                   wrap-params
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
