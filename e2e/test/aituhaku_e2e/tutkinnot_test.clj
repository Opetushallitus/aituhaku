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

(defn nakyvat-tutkinnot []
  (map w/text (w/find-elements (-> *ng*
                                 (.repeater "hakutulos in hakutulokset")
                                 (.column "hakutulos.nimi")))))

(deftest tutkinnot-test []
  (with-data {:koulutusalat [{:koodi "KA1"}]
              :opintoalat [{:koodi "OA1"
                            :koulutusala "KA1"}]
              :tutkinnot [{:nimi_fi "Haettava tutkinto A"
                           :tutkintotunnus "TUA"
                           :opintoala "OA1"},
                          {:nimi_fi "Haettava tutkinto B"
                           :tutkintotunnus "TUB"
                           :opintoala "OA1"}]}
    (with-webdriver
      (testing
        "tutkinnot:"
        (avaa-aituhaku tutkinnot)
        (testing
          "hae nimellä:"
          (hae-nimella "haettava tutkinto")
          (testing
            "pitäisi näyttää lista tutkinnoista"
            (w/wait-until #(subset? #{"Haettava tutkinto A" "Haettava tutkinto B"}
                                    (set (nakyvat-tutkinnot))))))))))
