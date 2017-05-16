(ns aituhaku.test-util
  (:require
    [aituhaku.asetukset :as haku-asetukset]
    [aituhaku.palvelin :as palvelin]
    [cheshire.core :as cheshire]
    [peridot.core :as peridot]))

(defn body-json [response]
 (cheshire/parse-string (slurp (:body response) :encoding "UTF-8") true))

(defn peridot-session! []
  (let [asetukset
        (-> haku-asetukset/oletusasetukset
          (assoc :development-mode true))
        crout (palvelin/app asetukset)
        _ (deliver haku-asetukset/asetukset asetukset)]
    (peridot/session crout)))

(defn alusta-korma!
  ([asetukset]
    (let [db-asetukset (merge-with #(or %2 %1)
                         (:db asetukset)
                         {:host (System/getenv "AITUHAKU_DB_HOST")
                          :port (System/getenv "AITUHAKU_DB_PORT")
                          :name (System/getenv "AITUHAKU_DB_NAME")
                          :user (System/getenv "AITUHAKU_DB_USER")
                          :password (System/getenv "AITUHAKU_DB_PASSWORD")})]
      (aituhaku.arkisto.sql.korma/luo-db db-asetukset)))
    ([]
    (let [dev-asetukset (assoc haku-asetukset/oletusasetukset :development-mode true)
          asetukset (haku-asetukset/lue-asetukset dev-asetukset)]
      (alusta-korma! asetukset))))

(defn with-korma [f]
 (let [asetukset
       (-> haku-asetukset/oletusasetukset
         (assoc :development-mode true))
       _ (alusta-korma! asetukset)]
   (f)))