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

(ns aituhaku-e2e.tutkintosivu-test
  (:require [clojure.test :refer :all]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.data-util :refer [with-data]]
            [aituhaku-e2e.util :refer :all]
            [aitu-e2e.datatehdas :refer [tutkinnot-oletus-testidata]]))

(defn tutkintosivu [tutkintotunnus]
  (str "/fi/#/tutkinto/" tutkintotunnus))

(defn tutkinnon-tietokentan-arvo [kentta]
  (-> *ng*
      (.binding kentta)
      w/find-element
      w/text))

(defn tutkinnon-jarjestajien-nimet []
  (map w/text (-> *ng*
                  (.repeater "jarjestaja in tutkinto.jarjestajat")
                  (.column "jarjestaja.nimi")
                  w/find-elements)))

(defn tutkinto-sivun-otsikko []
  (w/text "h2"))

(deftest tutkintosivu-test
  (let [testidata (luo-sopimukset-tutkinnoille (tutkinnot-oletus-testidata))
        tutkinto (-> testidata :tutkinnot first)]
    (with-webdriver
      (with-data testidata
        (avaa (tutkintosivu (:tutkintotunnus tutkinto)))
        (testing
          "sivun otsikko näkyy"
          (is (= (.toLowerCase (tutkinto-sivun-otsikko)) (.toLowerCase (:nimi_fi tutkinto)))))
        (testing
          "tutkinnon tiedot näkyvät oikein"
          (testing
            "tutkintotunnus"
            (is (= (tutkinnon-tietokentan-arvo "tutkinto.tutkintotunnus") (:tutkintotunnus tutkinto))))
          (testing
            "koulutusalan nimi"
            (is (= (w/text (w/find-element {:css ".e2e-tutkinto-koulutusala-nimi"}))
                   (get-in testidata [:koulutusalat 0 :selite_fi]))))
          (testing
            "opintoalan nimi"
            (is (= (w/text (w/find-element {:css ".e2e-tutkinto-opintoala-nimi"}))
                   (get-in testidata [:opintoalat 0 :selite_fi]))))
          (testing
            (is (= (w/text (w/find-element {:css ".e2e-toimikunta-nimi"})) (get-in testidata [:toimikunnat 0 :nimi_fi]))))
          (testing
            "tutkinnon järjestäjät"
            (is (= (set (tutkinnon-jarjestajien-nimet)) (set (map :nimi (:oppilaitokset testidata)))))))))))

(defn tutkinto [tutkintotunnus siirtymaajan-loppupvm voimassa-loppupvm]
  (luo-sopimukset-tutkinnoille {:koulutusalat [{:koodi "KA"
                                                :selite_fi "Koulutusala"}]
                                :opintoalat [{:koodi "OA"
                                              :selite_fi "Opintoala"
                                              :koulutusala "KA"}]
                                :toimikunnat [{:tkunta "TK"}]
                                :koulutustoimijat [{:ytunnus "1234567-8"}]
                                :oppilaitokset [{:oppilaitoskoodi "12345"
                                                 :koulutustoimija "1234567-8"}]
                                :tutkinnot [{:tutkintotunnus tutkintotunnus
                                             :opintoala "OA"
                                             :nimi_fi "Tutkinto"
                                             :voimassa_alkupvm "1899-01-01"
                                             :siirtymaajan_loppupvm siirtymaajan-loppupvm
                                             :voimassa_loppupvm voimassa-loppupvm}]}))

(defn voimassaoleva-tutkinto [tutkintotunnus]
  (tutkinto tutkintotunnus "2199-01-01" "2100-01-01"))

(defn siirtymaajalla-oleva-tutkinto [tutkintotunnus siirtymaajan-loppupvm]
  (tutkinto tutkintotunnus siirtymaajan-loppupvm "2100-01-01"))

(defn vanhentunut-tutkinto [tutkintotunnus]
  (tutkinto tutkintotunnus "2000-01-01" "2000-01-01"))

(defn huomautus-nakyvissa? [re]
  (when-let [h (w/find-element {:css ".huom"})]
     (and (w/visible? h)
          (re-find re (w/text h)))))

(defn ilmoitus-siirtymaajasta-nakyvissa? []
  (huomautus-nakyvissa? #"siirtymäajalla"))

(defn ilmoitus-vanhentumisesta-nakyvissa? []
  (huomautus-nakyvissa? #"vanhentunut"))

(defn nakyva-siirtymaajan-paattymispvm []
  (some-> (w/find-element {:css ".huom"})
    w/text
    (->> (re-find #"[0-9]{1,2}\.[0-9]{1,2}\.[0-9]{4}"))))

(deftest voimassaoleva-ilmoitus-test
  (with-webdriver
    (testing "Voimassaolevalle tutkinnolle ei näytetä vanhentumiseen liittyviä ilmoituksia"
       (with-data (voimassaoleva-tutkinto "123")
         (avaa (tutkintosivu "123"))
         (is (not (ilmoitus-siirtymaajasta-nakyvissa?)))
         (is (not (ilmoitus-vanhentumisesta-nakyvissa?)))))))

(deftest siirtymaajalla-ilmoitus-test
  (with-webdriver
    (testing "Siirtymäajalla olevalle tutkinnolle näytetään ilmoitus siirtymäajasta"
      (with-data (siirtymaajalla-oleva-tutkinto "456" "2100-01-01")
        (avaa (tutkintosivu "456"))
        (is (ilmoitus-siirtymaajasta-nakyvissa?))
        (is (= (nakyva-siirtymaajan-paattymispvm) "1.1.2100"))
        (is (not (ilmoitus-vanhentumisesta-nakyvissa?)))))))
