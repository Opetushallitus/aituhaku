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

(defn with-korma [f]
 (let [asetukset
       (-> haku-asetukset/oletusasetukset
         (assoc :development-mode true))
       _ (alusta-korma! asetukset)]
   (f)))

(defn time-with-peridot-multi
  "Provides simple performance test capabilities for Peridot. Runs random selection of URLs using specified amount of threads and reports results."
  [url-seq thread-count urls-per-thread]
  (let [asetukset
        (-> haku-asetukset/oletusasetukset
          (assoc :development-mode true))
        crout (palvelin/app asetukset)
        _ (deliver haku-asetukset/asetukset asetukset)]
    
    (let [peridot-session (peridot/session crout)
          start (java.lang.System/currentTimeMillis)
          req-count (* thread-count urls-per-thread)]
      (let [urls-for-threads (for [_ (range thread-count)]
                               (repeatedly urls-per-thread #(rand-nth url-seq)))]
        (doall
          (map deref
            (doall
              (for [thread-url-seq urls-for-threads]
                (future
                  (doseq [url url-seq]
                    (let [response (peridot/request peridot-session url :request-method :get)]
                      (is (= (:status (:response response)) 200))))))))))
      (let [end (java.lang.System/currentTimeMillis)
            elapsed (- end start)
            avg (float (/ elapsed req-count))
            throughput (float (/ 1000 avg))]
        (log/info "average request time " avg " ms")
        (log/info "request per second, avg " throughput)))))
 
(defn oppilaitos->jarjestaja-url 
  [oppilaitos]
  (str "/api/jarjestaja/" (:oppilaitoskoodi oppilaitos)))

(deftest ^:integraatio jarjestaja-tiedot-yksi-saie []
  (with-korma
    #(let [urlit (map oppilaitos->jarjestaja-url (take 100 (jarjestaja-arkisto/hae-oppilaitoskoodit)))]
      (time-with-peridot-multi urlit 1 100))))

(deftest ^:integraatio jarjestaja-tiedot-useita-saikeita []  
  (with-korma
    #(let [urlit (map oppilaitos->jarjestaja-url (take 500 (jarjestaja-arkisto/hae-oppilaitoskoodit)))]
      (time-with-peridot-multi urlit 5 100))))
