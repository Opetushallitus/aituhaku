(ns aituhaku-e2e.tutkinnot-test
  (:require [clojure.test :refer [deftest is testing]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.datatehdas :refer [tutkinnot-oletus-testidata]]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.data-util :refer [with-data]]
            [aituhaku-e2e.util :refer :all]
            [clojure.string :refer [blank?]]))

(def tutkinnot "/#/tutkinnot")

(defn hae-nimella [nimi]
  (tyhjenna-input "hakuehto.nimi")
  (w/input-text (str "input[ng-model=\"hakuehto.nimi\"]") nimi)
  (odota-angular-pyyntoa))

(defn hae-opintoalalla [opintoala]
  (tyhjenna-input "hakuehto.opintoala")
  (w/input-text (str "input[ng-model=\"hakuehto.opintoala\"]") opintoala)
  (odota-angular-pyyntoa))

(defn nakyvien-tutkintojen-tiedot [kentta]
  (map w/text (w/find-elements (-> *ng*
                                   (.repeater "hakutulos in hakutulokset")
                                   (.column kentta)))))

(defn seuraava-sivu []
  (w/click {:text "Seuraava"})
  (odota-angular-pyyntoa))

(defn hakutuloslaskurin-teksti []
  (first
    (remove blank? (map w/text (w/find-elements {:css ".hakutuloslaskuri p"})))))

(defn testaa-hakutuloslistan-sivua [tutkinnot-osajoukko]
  (is (= (nakyvien-tutkintojen-tiedot "hakutulos.nimi")
         (map :nimi_fi tutkinnot-osajoukko)))
  (is (= (nakyvien-tutkintojen-tiedot "hakutulos.tutkintotunnus")
         (map :tutkintotunnus tutkinnot-osajoukko)))
  (is (every? #(= % "Opintoala 1") (nakyvien-tutkintojen-tiedot "hakutulos.opintoala_nimi_fi"))))

(deftest tutkinnot-haku-test
  (let [testidata (tutkinnot-oletus-testidata)
        vaara-hakuehto "väärä hakuehto"
        tutkinnon-nimi-ehto (-> (:tutkinnot testidata) first :nimi_fi)
        opintoalan-nimi-ehto (-> (:opintoalat testidata) first :selite_fi)]
    (with-data testidata
      (with-webdriver
        (avaa-aituhaku tutkinnot)
        (testing
          "sivun otsikko"
          (is (= (sivun-otsikko) "AITU | NÄYTTÖTUTKINTOHAKU")))
        (testing
          "hakutulokset:"
          (testing "nimellä haku"
            (testing
              "hakuehdolla ei löydy tuloksia"
              (hae-nimella vaara-hakuehto)
              (is (= (hakutuloslaskurin-teksti) "ei hakutuloksia")))
            (testing
              "hakuehdolla löytyy yksi tulos"
              (hae-nimella tutkinnon-nimi-ehto)
              (testaa-hakutuloslistan-sivua (take 1 (:tutkinnot testidata)))
              (is (= (hakutuloslaskurin-teksti) "1 hakutulos"))))
          (tyhjenna-input "hakuehto.nimi")
          (testing "opintoalalla haku"
            (testing
              "hakuehdolla ei löydy tuloksia"
              (hae-opintoalalla vaara-hakuehto)
              (is (= (hakutuloslaskurin-teksti) "ei hakutuloksia")))
            (testing
              "hakuehdolla löytyy tuloksia"
              (hae-opintoalalla opintoalan-nimi-ehto)
              (testaa-hakutuloslistan-sivua (take 10 (:tutkinnot testidata)))
              (is (= (hakutuloslaskurin-teksti) "hakutulokset 1 - 10 / yhteensä 25 hakutulosta"))))
          (testing "nimellä ja opintoalalla haku"
            (testing
              "molemmat ehdot eivät täsmää"
              (hae-nimella tutkinnon-nimi-ehto)
              (hae-opintoalalla vaara-hakuehto)
              (is (= (hakutuloslaskurin-teksti) "ei hakutuloksia")))
            (testing
              "molemmat ehdot täsmäävät"
              (hae-nimella tutkinnon-nimi-ehto)
              (hae-opintoalalla opintoalan-nimi-ehto)
              (testaa-hakutuloslistan-sivua (take 1 (:tutkinnot testidata)))
              (is (= (hakutuloslaskurin-teksti) "1 hakutulos")))))))))

(deftest tutkinnot-sivutus-test
  (let [testidata (tutkinnot-oletus-testidata)]
    (with-data testidata
      (with-webdriver
        (avaa-aituhaku tutkinnot)
          (testing
            "sivutus"
            (hae-nimella "haettava tutkinto")
            (testing
              "ensimmäinen sivu:"
              (testaa-hakutuloslistan-sivua (take 10 (:tutkinnot testidata)))
              (is (= (hakutuloslaskurin-teksti) "hakutulokset 1 - 10 / yhteensä 25 hakutulosta")))
            (seuraava-sivu)
            (testing
              "toinen sivu:"
              (testaa-hakutuloslistan-sivua (take 10 (drop 10 (:tutkinnot testidata))))
              (is (= (hakutuloslaskurin-teksti) "hakutulokset 11 - 20 / yhteensä 25 hakutulosta")))
            (seuraava-sivu)
            (testing
              "kolmas sivu:"
              (testaa-hakutuloslistan-sivua (take 5 (drop 20 (:tutkinnot testidata))))
              (is (= (hakutuloslaskurin-teksti) "hakutulokset 21 - 25 / yhteensä 25 hakutulosta")))
          (testing
            "tutkinnon sivulle siirtyminen"
            (let [haettavan-tutkinnon-nimi "Haettava tutkinto a1"]
              (hae-nimella haettavan-tutkinnon-nimi)
              (w/click {:text haettavan-tutkinnon-nimi})
              (odota-angular-pyyntoa)
              (is (= (sivun-otsikko) "HAETTAVA TUTKINTO A1")))))))))
