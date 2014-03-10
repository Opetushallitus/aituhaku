(ns aituhaku-e2e.kielen_vaihto_test
  (:require [clojure.test :refer [deftest is testing]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.datatehdas :refer [tutkinnot-oletus-testidata]]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.data-util :refer [with-data]]
            [aituhaku-e2e.util :refer :all]))

(defn avaa-aituhaku-suomeksi [] (avaa-aituhaku "/fi/#/tutkinnot"))
(defn avaa-aituhaku-ruotsiksi [] (avaa-aituhaku "/sv/#/tutkinnot"))

(defn suomeksi-linkki-nakyvissa [] (w/exists? {:css ".fi.valittu a:not(.ng-hide)"}))
(defn suomeksi-span-nakyvissa [] (w/exists? {:css ".fi.ei-valittu span:not(.ng-hide)"}))
(defn ruotsiksi-linkki-nakyvissa [] (w/exists? {:css ".sv.valittu a:not(.ng-hide)"}))
(defn ruotsiksi-span-nakyvissa [] (w/exists? {:css ".sv.ei-valittu span:not(.ng-hide)"}))

(defn vaihda-kielta-suomeksi []
  (w/click {:css ".fi.valittu a"})
  (odota-angular-pyyntoa))

(defn vaihda-kielta-ruotsiksi []
  (w/click {:css ".sv.valittu a"})
  (odota-angular-pyyntoa))

(deftest kielenvaihto-test
  (testing "kielenvaihto"
    (testing "/fi/ url:iin mentäessä FI- linkki ei ole näkyvissä ja SV- linkki on näkyvissä."
      (with-webdriver
        (avaa-aituhaku-suomeksi)
        (is (not (suomeksi-linkki-nakyvissa)))
        (is (suomeksi-span-nakyvissa))
        (is (not(ruotsiksi-span-nakyvissa)))
        (is (ruotsiksi-linkki-nakyvissa))))
    (testing "/sv/ url:iin mentäessä SV- linkki ei ole näkyvissä ja FI- linkki on näkyvissä."
      (with-webdriver
        (avaa-aituhaku-ruotsiksi)
        (is (suomeksi-linkki-nakyvissa))
        (is (not (suomeksi-span-nakyvissa)))
        (is (ruotsiksi-span-nakyvissa))
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
