(ns aituhaku-e2e.toimikunta-test
  (:require [clojure.set :refer [subset?]]
            [clojure.test :refer [deftest is are testing]]
            [clj-webdriver.taxi :as w]
            [clj-time.core :as time]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.data-util :refer [with-data]]
            [aituhaku-e2e.util :refer :all]))

(defn toimikuntasivu [tkunta]
  (str "/fi/#/toimikunta/" tkunta))

(deftest toimikuntasivu-test
  (testing "toimikuntasivu"
    (with-webdriver
      ;; Oletetaan, että
      (with-data {:toimikunnat [{:nimi_fi "Ilmastointialan tutkintotoimikunta"
                                 :kielisyys "fi"
                                 :sahkoposti "toimikunta@mail.fi"
                                 :toimikausi_alku "2013-08-01"
                                 :toimikausi_loppu "2016-07-31"
                                 :tkunta "ILMA"}]}
        ;; Kun
        (avaa-aituhaku (toimikuntasivu "ILMA"))
        ;;Niin
        (are [elementti teksti] (= (elementin-teksti elementti) teksti)
             "toimikunta.nimi_fi" "ILMASTOINTIALAN TUTKINTOTOIMIKUNTA"
             "i18n.kielisyys[toimikunta.kielisyys]" "suomi"
             "toimikunta.toimikausi_alku" "01.08.2013 – 31.07.2016")))))
