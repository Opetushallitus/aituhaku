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

(ns aituhaku.arkisto.sql.korma
  (:import java.sql.Date
           org.joda.time.LocalDate)
  (:require korma.db
            [korma.core :as sql]
            [clj-time.coerce :as time-coerce]
            [clj-time.core :as time]))

(defn korma-asetukset
  "Muuttaa asetustiedoston db-avaimen arvon Korman odottamaan muotoon. Tunnus
kertoo mitä käyttäjätunnusta yhteyteen käytetään, esim. :user ->
\"db.account.user\", :admin -> \"db.account.admin\"."
  [db-asetukset]
  {:host (:host db-asetukset)
   :port (Integer/parseInt (:port db-asetukset))
   :db (:name db-asetukset)
   :user (:user db-asetukset)
   :password (:password db-asetukset)
   :minimum-pool-size (Integer/parseInt (:minimum-pool-size db-asetukset))
   :maximum-pool-size (Integer/parseInt (:maximum-pool-size db-asetukset))})

(defn datasource
  [db-asetukset]
  (let [korma-pool (:datasource (korma.db/connection-pool (korma.db/postgres (korma-asetukset db-asetukset))))]
    (.setCheckoutTimeout korma-pool 2000)
    (.setTestConnectionOnCheckout korma-pool true)
    {:make-pool? false
     :datasource korma-pool}))

(defn luo-db [db-asetukset]
  (korma.db/default-connection
    (korma.db/create-db (datasource db-asetukset))))

(defn convert-instances-of [c f m]
  (clojure.walk/postwalk #(if (instance? c %) (f %) %) m))

(defn joda-datetime->sql-timestamp [m]
  (convert-instances-of org.joda.time.DateTime
                        time-coerce/to-sql-time
                        m))

(defn sql-timestamp->joda-datetime [m]
  (convert-instances-of java.sql.Timestamp
                        time-coerce/from-sql-time
                        m))

(defn ^:private to-local-date-default-tz
  [date]
  (let [dt (time-coerce/to-date-time date)]
    (time-coerce/to-local-date (time/to-time-zone dt (time/default-time-zone)))))

(defn sql-date->joda-date [m]
  (convert-instances-of java.sql.Date
                        to-local-date-default-tz
                        m))

(defn joda-date->sql-date [m]
  (convert-instances-of org.joda.time.LocalDate
                        time-coerce/to-sql-date
                        m))

(defmacro defentity
  "Wrapperi Korman defentitylle, lisää yleiset prepare/transform-funktiot."
  [ent & body]
  `(sql/defentity ~ent
     (sql/prepare joda-date->sql-date)
     (sql/prepare joda-datetime->sql-timestamp)
     (sql/transform sql-date->joda-date)
     (sql/transform sql-timestamp->joda-datetime)
     ~@body))

(declare tutkinnot_view)

(defentity toimikunta_view
  (sql/table :aituhaku.toimikuntaview))

(defentity tutkinnon_jarjestajat_view
  (sql/table :aituhaku.tutkinnon_jarjestajat_view :jarjestajat))

(defentity tutkinnon_toimikunnat_view
  (sql/table :aituhaku.tutkinnon_toimikunnat_view :toimikunnat))

(defentity tutkinnot_view
  (sql/table :aituhaku.tutkinnot_view)
  (sql/pk :tutkintotunnus)
  (sql/has-many tutkinnon_jarjestajat_view
    {:fk :tutkintotunnus})
  (sql/has-many tutkinnon_toimikunnat_view
    {:fk :tutkintotunnus}))
