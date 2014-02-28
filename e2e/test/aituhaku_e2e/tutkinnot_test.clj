(ns aituhaku-e2e.tutkinnot-test
  (:require [clojure.set :refer [subset?]]
            [clojure.test :refer [deftest is testing]]
            [clj-webdriver.taxi :as w]
            [clj-time.core :as time]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.data-util :refer [with-data]]
            [aituhaku-e2e.util :refer :all]
            [clojure.string :refer [blank?]]))

(def tutkinnot "/#/tutkinnot")

(defn hae-nimella [nimi]
  (tyhjenna-input "hakuehto.nimi")
  (w/input-text (str "input[ng-model=\"hakuehto.nimi\"]") nimi)
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
                 :koulutusala "KA1"}]
   :tutkinnot (luo-tutkintoja-opintoalaan 25 "OA1")})

(defn testaa-halutuloslistan-sivua [tutkinnot-osajoukko laskurin-teksti]
  (w/wait-until #(= (hakutuloslaskurin-teksti) laskurin-teksti))
  (is (=
        (nakyvien-tutkintojen-tiedot "hakutulos.nimi")
        (map :nimi_fi tutkinnot-osajoukko)))
  (is (=
        (nakyvien-tutkintojen-tiedot "hakutulos.tutkintotunnus")
        (map :tutkintotunnus tutkinnot-osajoukko)))
  (is (every? #(= % "Opintoala 1") (nakyvien-tutkintojen-tiedot "hakutulos.opintoala_nimi_fi"))))

(deftest tutkinnot-test
  (with-data tutkinnot-testidata
    (with-webdriver
      (testing
        "sivutus:"
        (avaa-aituhaku tutkinnot)
        (testing
          "sivun otsikko"
          (is (= (sivun-otsikko) "AITU | NÄYTTÖTUTKINTOHAKU")))
        (testing
          "hakuehdolla ei löydy tuloksia"
          (hae-nimella "väärä hakuehto")
          (w/wait-until #(= (hakutuloslaskurin-teksti) "ei hakutuloksia")))
        (testing
          "hakuehdolla löytyy yksi tulos"
          (hae-nimella "haettava tutkinto a1")
          (testaa-halutuloslistan-sivua
            (take 1 (:tutkinnot tutkinnot-testidata))
            "1 hakutulos"))
        (testing
          "hakuehdolla löytyy paljon tutkintoja"
          (hae-nimella "haettava tutkinto")
          (testing
            "ensimmäinen sivu:"
            (testaa-halutuloslistan-sivua
              (take 10 (:tutkinnot tutkinnot-testidata))
              "hakutulokset 1 - 10 / yhteensä 25 hakutulosta"))
          (seuraava-sivu)
          (testing
            "toinen sivu:"
            (testaa-halutuloslistan-sivua
              (take 10 (drop 10 (:tutkinnot tutkinnot-testidata)))
              "hakutulokset 11 - 20 / yhteensä 25 hakutulosta"))
          (seuraava-sivu)
          (testing
            "kolmas sivu:"
            (testaa-halutuloslistan-sivua
              (take 5 (drop 20 (:tutkinnot tutkinnot-testidata)))
              "hakutulokset 21 - 25 / yhteensä 25 hakutulosta")))
        (testing
          "tutkinnon sivulle siirtyminen"
          (let [haettavan-tutkinnon-nimi "Haettava tutkinto a1"]
            (hae-nimella haettavan-tutkinnon-nimi)
            (some-> (w/find-element {:text haettavan-tutkinnon-nimi}) w/text)
            (w/wait-until #(some-> (w/find-element {:text haettavan-tutkinnon-nimi}) w/text))
            (w/click {:text haettavan-tutkinnon-nimi})
            (odota-angular-pyyntoa)
            (is (= (sivun-otsikko) "HAETTAVA TUTKINTO A1"))))))))
