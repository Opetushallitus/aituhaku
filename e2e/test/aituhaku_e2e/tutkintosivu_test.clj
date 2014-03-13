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
  (:require [clojure.test :refer [deftest is testing]]
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

(defn toimikunta-kentan-arvo []
  (-> *ng*
      (.repeater "toimikunta in tutkinto.toimikunnat")
      (.column "toimikunta.nimi")
      w/find-element
      w/text))

(defn tutkinnon-jarjestajien-nimet []
  (map w/text (-> *ng*
                  (.repeater "jarjestaja in tutkinto.jarjestajat")
                  (.column "jarjestaja.nimi")
                  w/find-elements)))

(deftest tutkintosivu-test
  (let [testidata (tutkinnot-oletus-testidata)
        tutkinto (-> testidata :tutkinnot first)]
    (with-webdriver
      (with-data testidata
        (avaa-aituhaku (tutkintosivu (:tutkintotunnus tutkinto)))
        (testing
          "sivun otsikko näkyy"
          (is (= (.toLowerCase (sivun-otsikko)) (.toLowerCase (:nimi_fi tutkinto)))))
        (testing
          "tutkinnon tiedot näkyvät oikein"
          (testing
            "tutkintotunnus"
            (is (= (tutkinnon-tietokentan-arvo "tutkinto.tutkintotunnus") (:tutkintotunnus tutkinto))))
          (testing
            "koulutusalan nimi"
            (is (= (tutkinnon-tietokentan-arvo "tutkinto.koulutusala_nimi")
                   (get-in testidata [:koulutusalat 0 :selite_fi]))))
          (testing
            "opintoalan nimi"
            (is (= (tutkinnon-tietokentan-arvo "tutkinto.opintoala_nimi")
                   (get-in testidata [:opintoalat 0 :selite_fi]))))
          (testing
            "toimikunta"
            (is (= (toimikunta-kentan-arvo) (get-in testidata [:toimikunnat 0 :nimi_fi]))))
          (testing
            "tutkinnon järjestäjät"
            (is (= (set (tutkinnon-jarjestajien-nimet)) (set (map :nimi (:oppilaitokset testidata)))))))))))
