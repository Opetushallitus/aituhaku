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

(defn peridot-session! []
  (let [asetukset
        (-> haku-asetukset/oletusasetukset
          (assoc :development-mode true))
        crout (palvelin/app asetukset)
        _ (deliver haku-asetukset/asetukset asetukset)]
    (peridot/session crout)))

(defn time-with-peridot-multi
  "Provides simple performance test capabilities for Peridot. Runs random selection of URLs using specified amount of threads and reports results."
  [url-seq thread-count urls-per-thread]
  (let [peridot-session (peridot-session!)
        start (java.lang.System/currentTimeMillis)
        req-count (* thread-count urls-per-thread)
        urls-for-threads (for [_ (range thread-count)]
                           (repeatedly urls-per-thread #(rand-nth url-seq))) 
        req-durations (doall ; pakotetaan evaluointi kutsumalla deref kaikille säikeille
                        (map deref
                          ; luodaan joukko säikeitä. Kaikki säikeet on luotu ennen kuin deref kutsutaan
                          (doall
                            (for [thread-url-seq urls-for-threads]
                              (future
                                (doall
                                  (for [url thread-url-seq]
                                    (try
                                      (let [start (java.lang.System/currentTimeMillis)
                                            response (peridot/request peridot-session url :request-method :get)
                                            end (java.lang.System/currentTimeMillis)
                                            elapsed (- end start)]
                                        (is (= (:status (:response response)) 200))
                                        elapsed)
                                      (catch Exception e
                                        ; timeout exception. TODO: täytyy miettiä miten tämä halutaan oikeasti käsitellä 
                                        ; pidennetään timeouttia? halutaanko raportoida timeoutin ylittäneet requestit jotenkin?
                                        (.printStackTrace e))))))))))
        end (java.lang.System/currentTimeMillis)
        elapsed (- end start)
        avg (float (/ (reduce + (flatten req-durations)) req-count))
        throughput (float (/ 1000 (/ elapsed req-count)))]
    (log/info "time elapsed " elapsed " ms")
    (log/info "number of threads " thread-count)
    (log/info "total requests " req-count)
    (log/info "average request time " avg " ms")
    (log/info "requests per second, avg " throughput)))

(defn oppilaitos->jarjestaja-url 
  [oppilaitos]
  (str "/api/jarjestaja/" (:oppilaitoskoodi oppilaitos)))

(deftest ^:integraatio jarjestaja-tiedot-yksi-saie []
  (log/info "järjestäjätiedot, yksi säie")
  (with-korma
    #(let [urlit (map oppilaitos->jarjestaja-url (take 100 (jarjestaja-arkisto/hae-oppilaitoskoodit)))]
      (time-with-peridot-multi urlit 1 100))))

(deftest ^:integraatio jarjestaja-tiedot-useita-saikeita []  
  (log/info "järjestäjätiedot, useita säikeitä")
  (with-korma
    #(let [urlit (map oppilaitos->jarjestaja-url (take 500 (jarjestaja-arkisto/hae-oppilaitoskoodit)))]
      (time-with-peridot-multi urlit 5 100))))

(defn termi->tutkintohaku-url
  [termi]
   (str "/api/tutkinto/haku?nimi=" (java.net.URLEncoder/encode termi "UTF-8")))

(defn termi->opintoalahaku-url
  [termi]
  (str "/api/opintoala/haku?termi=" (java.net.URLEncoder/encode termi "UTF-8") "&kieli=fi&_=1411987692933"))

(deftest ^:integraatio tutkintohaku []
  (log/info "tutkintohaku, useita säikeitä")
  ; sisältää myös termejä, joilla ei löydy tuloksia
  ; hakutermin oltava vähintään kolme merkkiä, jotta rajapinta palauttaa tuloksia
  (let [hakutermit (list "ase" "aut" "ope" "rav" "kul" "hev" "Ravinto" "Pinta" "opa" "xxx" "   " "yxzdfgasdlasqwrojaskn")]    
    (with-korma
      #(let [urlit (map termi->tutkintohaku-url hakutermit)]
        (time-with-peridot-multi urlit 15 100)))))

(deftest ^:integraatio opintoalahaku []
  (log/info "opintoalahaku, useita säikeitä")
  ; sisältää myös termejä, joilla ei löydy tuloksia
  (let [hakutermit (list "a" "e" "o" "x" "oi" "opet" "ase" "aa" "oi" "ra" "ala" "   " "yxzdfgasdlasqwrojaskn")]
    (with-korma
      #(let [urlit (map termi->opintoalahaku-url  hakutermit)]
        (time-with-peridot-multi urlit 15 100)))))

