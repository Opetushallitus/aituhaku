(ns aituhaku.rest.performance-test
   (:require 
    [aituhaku.asetukset :as haku-asetukset]
    [aituhaku.palvelin :as palvelin]
    [clojure.test :refer :all]
    [peridot.core :as peridot]
    [clojure.tools.logging :as log]
    [aituhaku.arkisto.sql.jarjestaja :as jarjestaja-arkisto]
    ))


(defn alusta-korma!
  ([asetukset]
    (let [db-asetukset (merge-with #(or %2 %1)
                         (:db asetukset)
                         {:host (System/getenv "AMTU_DB_HOST")
                          :port (System/getenv "AMTU_DB_PORT")})]
      (aituhaku.arkisto.sql.korma/luo-db db-asetukset)))
    ([]
    (let [dev-asetukset (assoc haku-asetukset/oletusasetukset :development-mode true)
          asetukset (haku-asetukset/lue-asetukset dev-asetukset)]
      (alusta-korma! asetukset))))
 
(deftest jarjestaja-tiedot-yksi-saie []  
  (let [asetukset
        (-> haku-asetukset/oletusasetukset
          (assoc :development-mode true))
        _ (alusta-korma! asetukset)
        crout (palvelin/app asetukset)
        _ (deliver haku-asetukset/asetukset asetukset)]

    (let [peridot-session (peridot/session crout)
          start (java.lang.System/currentTimeMillis)]
      (doseq [oppilaitos (take 100 (jarjestaja-arkisto/hae-oppilaitoskoodit))]
          (let [response (peridot/request peridot-session (str "/api/jarjestaja/" (:oppilaitoskoodi oppilaitos)) :request-method :get)]
            (is (= (:status (:response response)) 200))
            ))
      (let [end (java.lang.System/currentTimeMillis)
            elapsed (- end start)
            avg (float (/ elapsed 100))]
        (log/info "average request time " avg " ms")
        ))))
