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

(ns aituhaku-e2e.kielen_vaihto_test
  (:require [clojure.test :refer [deftest is testing]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.datatehdas :refer [tutkinnot-oletus-testidata]]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.data-util :refer [with-data]]
            [aituhaku-e2e.util :refer :all]))

(defn avaa-aituhaku-suomeksi [] (avaa-aituhaku "/fi/#/tutkinnot"))
(defn avaa-aituhaku-ruotsiksi [] (avaa-aituhaku "/sv/#/tutkinnot"))

(defn suomeksi-linkki-nakyvissa [] (w/exists? {:css "#fi-link:not(.ng-hide)"}))
(defn ruotsiksi-linkki-nakyvissa [] (w/exists? {:css "#sv-link:not(.ng-hide)"}))

(defn vaihda-kielta-suomeksi []
  (w/click {:css "#fi-link"})
  (w/wait-until #(ruotsiksi-linkki-nakyvissa)))

(defn vaihda-kielta-ruotsiksi []
  (w/click {:css "#sv-link"})
  (w/wait-until #(suomeksi-linkki-nakyvissa)))

(deftest kielenvaihto-test
  (testing "kielenvaihto"
    (testing "/fi/ url:iin mentäessä Suomeksi-linkki ei ole näkyvissä ja På svenska- linkki on näkyvissä."
      (with-webdriver
        (avaa-aituhaku-suomeksi)
        (is (not (suomeksi-linkki-nakyvissa)))
        (is (ruotsiksi-linkki-nakyvissa))))
    (testing "/sv/ url:iin mentäessä På svenska- linkki ei ole näkyvissä ja Suomeksi- linkki on näkyvissä."
      (with-webdriver
        (avaa-aituhaku-ruotsiksi)
        (is (suomeksi-linkki-nakyvissa))
        (is (not (ruotsiksi-linkki-nakyvissa)))))
    (testing "kielen vaihtaminen suomesta ruotsiin onnistuu"
      (with-webdriver
        (avaa-aituhaku-suomeksi)
        (is (not (suomeksi-linkki-nakyvissa)))
        (vaihda-kielta-ruotsiksi)
        (is (suomeksi-linkki-nakyvissa))))
    (testing "kielen vaihtaminen ruotsista suomeen onnistuu"
      (with-webdriver
        (avaa-aituhaku-ruotsiksi)
        (is (not (ruotsiksi-linkki-nakyvissa)))
        (vaihda-kielta-suomeksi)
        (is (ruotsiksi-linkki-nakyvissa))))))
