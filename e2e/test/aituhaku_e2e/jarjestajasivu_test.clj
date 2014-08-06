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

(ns aituhaku-e2e.jarjestajasivu-test
  (:require [clojure.test :refer :all]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.data-util :refer [with-data]]
            [aituhaku-e2e.util :refer :all]
            [aitu-e2e.datatehdas :refer [tutkinnot-oletus-testidata]]))

(defn jarjestajasivu [oppilaitoskoodi]
  (str "/fi/#/jarjestaja/" oppilaitoskoodi))

(defn nakyvien-tutkintojen-nimet []
  (set (map w/text (w/find-elements (-> *ng*
                                       (.repeater "hakutulos in hakutulokset")
                                       (.column "hakutulos.nimi"))))))

(deftest jarjestajasivu-test
  (let [testidata (tutkinnot-oletus-testidata)
        oppilaitos (-> testidata :oppilaitokset first)
        oppilaitoksen-jarjestamissopimukset (filter (fn [x] (= (:oppilaitoskoodi oppilaitos) (:tutkintotilaisuuksista_vastaava_oppilaitos x))) (:jarjestamissopimukset testidata))
        sopimuksien-toimikunnat (set (map :sopijatoimikunta oppilaitoksen-jarjestamissopimukset))
        toimikunnat-ja-tutkinnot (filter (fn [x] (contains? sopimuksien-toimikunnat (:toimikunta x))) (:toimikunta_ja_tutkinto testidata))
        tutkintotunnukset (set (map :tutkintotunnus toimikunnat-ja-tutkinnot))
        oppilaitoksen-tutkinnot (filter (fn [x] (contains? tutkintotunnukset (:tutkintotunnus x))) (:tutkinnot testidata))]
    (with-webdriver
      (with-data testidata
        (avaa (jarjestajasivu (:oppilaitoskoodi oppilaitos)))
        (testing "Oppilaitoksen nimi on otsikkona"
          (is (= (.toLowerCase (w/text "h2")) (.toLowerCase (:nimi oppilaitos)))))
        (testing "Oppilaitoksen tutkinnot täsmäävät"
          (is (= (nakyvien-tutkintojen-nimet) (set (map :nimi_fi oppilaitoksen-tutkinnot)))))))))
