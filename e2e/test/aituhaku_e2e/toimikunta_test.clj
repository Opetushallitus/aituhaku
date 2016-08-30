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

(ns aituhaku-e2e.toimikunta-test
  (:require [clojure.set :refer [subset?]]
            [clojure.test :refer [deftest is are testing]]
            [clj-webdriver.taxi :as w]
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
                                 :toimikausi_alku "2016-08-01"
                                 :toimikausi_loppu "2018-07-31"
                                 :toimikausi 3
                                 :tkunta "ILMA"}]
                  :henkilot [{:henkiloid -1
                              :puhelin "0401234567"
                              :osoite "Testikatu 1"
                              :postinumero "12345"
                              :postitoimipaikka "Testilä"}]
                  :jasenet [{:henkilo {:henkiloid -1}
                             :rooli "sihteeri"
                             :toimikunta "ILMA"}]}
        ;; Kun
        (avaa (toimikuntasivu "ILMA"))
        ;;Niin
        (are [elementti teksti] (= (elementin-teksti elementti) teksti)
             "i18n.kielisyys[toimikunta.kielisyys]" "suomi")
        (are [css teksti] (= (w/text (w/find-element {:css css})) teksti)
                          ".e2e-toimikunta-nimi" "ILMASTOINTIALAN TUTKINTOTOIMIKUNTA"
                          ".e2e-toimikausi" "01.08.2016 – 31.07.2018"
                          ".e2e-puhelin" "0401234567"
                          ".e2e-osoite" "Ilmastointialan tutkintotoimikunta\nTestikatu 1\n12345 Testilä")))))
