(ns aituhaku-e2e.tutkinnot-test
  (:require [clojure.set :refer [subset?]]
            [clojure.test :refer [deftest is testing]]
            [clj-webdriver.taxi :as w]
            [clj-time.core :as time]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.data-util :refer [with-data]]
            [aituhaku-e2e.util :refer :all]))

(def tutkinnot "/#/tutkinnot")

(defn hae-nimella [nimi]
  (tyhjenna-input "hakuehto.nimi")
  (w/input-text (str "input[ng-model=\"hakuehto.nimi\"]") nimi)
  (odota-angular-pyyntoa))

(defn nakyvat-tutkintojen-nimet []
  (map w/text (w/find-elements (-> *ng*
                                 (.repeater "hakutulos in hakutulokset")
                                 (.column "hakutulos.nimi")))))

(defn nakyvat-tutkintotunnukset []
  (map w/text (w/find-elements (-> *ng*
                                   (.repeater "hakutulos in hakutulokset")
                                   (.column "hakutulos.tutkintotunnus")))))

(defn nakyvat-opintoalat []
  (map w/text (w/find-elements (-> *ng*
                                   (.repeater "hakutulos in hakutulokset")
                                   (.column "hakutulos.opintoala_nimi")))))
(defn seuraava-sivu []
  (w/click {:css ".pagination li:nth-child(3) a"})
  (odota-angular-pyyntoa))

(defn nykyinen-sivu []
  (w/attribute ".table.hakutulokset .table-header" "data-nykyinen-sivu"))

(defn hakutuloslaskurin-teksti []
  (first (map w/text (w/find-elements {:css ".hakutuloslaskuri p"}))))

(defn ei-hakutuloksia-teksti []
  (first (map w/text (w/find-elements {:css ".hakutuloslaskuri p + p"}))))

(defn luo-tutkintoja-opintoalaan [lkm opintoala]
  (take lkm
        (for [a (seq "abcdefghijklmnopqrstuwvxyz")
              i (range 9)]
          {:nimi_fi (str "Haettava tutkinto " a (+ i 1 ))
           :tutkintotunnus (str "TU" a (+ i 1 ) )
           :opintoala opintoala})))

(def tutkinnot-testidata
  {:koulutusalat [{:koodi "KA1"}]
   :opintoalat [{:koodi "OA1"
                 :koulutusala "KA1"}
                {:koodi "OA2"
                 :koulutusala "KA1"}]
   :tutkinnot (luo-tutkintoja-opintoalaan 25 "OA1")})

(deftest tutkinnot-test
  (with-data tutkinnot-testidata
    (with-webdriver
      (testing
        "sivutus:"
        (avaa-aituhaku tutkinnot)
        (testing
          "hakuehdolla ei löydy tuloksia"
          (hae-nimella "väärä hakuehto")
          (w/wait-until #(= (ei-hakutuloksia-teksti) "ei hakutuloksia")))
        (testing
          "hakuehdolla löytyy tuloksia"
          (hae-nimella "haettava tutkinto")
          (testing
            "ensimmäinen sivu:"
            (let [tutkinnot-osajoukko (take 10 (:tutkinnot tutkinnot-testidata))]
              (w/wait-until #(= (hakutuloslaskurin-teksti) "hakutulokset 1 - 10 / yhteensä 25 hakutulosta"))
              (is (=
                    (nakyvat-tutkintojen-nimet)
                    (map :nimi_fi tutkinnot-osajoukko)))
              (is (=
                    (nakyvat-tutkintotunnukset)
                    (map :tutkintotunnus tutkinnot-osajoukko)))
              (is (every? #(= % "Opintoala 1") (nakyvat-opintoalat)))))
          (seuraava-sivu)
          (testing
            "toinen sivu:"
            (let [tutkinnot-osajoukko (take 10 (drop 10 (:tutkinnot tutkinnot-testidata)))]
              (w/wait-until #(= (hakutuloslaskurin-teksti) "hakutulokset 11 - 20 / yhteensä 25 hakutulosta"))
              (is (=
                    (nakyvat-tutkintojen-nimet)
                    (map :nimi_fi tutkinnot-osajoukko)))
              (is (=
                    (nakyvat-tutkintotunnukset)
                    (map :tutkintotunnus tutkinnot-osajoukko)))
              (is (every? #(= % "Opintoala 1") (nakyvat-opintoalat)))))
          (seuraava-sivu)
          (testing
            "kolmas sivu:"
            (let [tutkinnot-osajoukko (take 5 (drop 20 (:tutkinnot tutkinnot-testidata)))]
              (w/wait-until #(= (hakutuloslaskurin-teksti) "hakutulokset 21 - 25 / yhteensä 25 hakutulosta"))
              (is (=
                    (nakyvat-tutkintojen-nimet)
                    (map :nimi_fi tutkinnot-osajoukko)))
              (is (=
                    (nakyvat-tutkintotunnukset)
                    (map :tutkintotunnus tutkinnot-osajoukko)))
              (is (every? #(= % "Opintoala 1") (nakyvat-opintoalat))))))))))
