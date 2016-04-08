(ns aituhaku.arkisto.tutkinto-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as check]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clj-time.core :as time]
            clj-time.coerce
            [aituhaku.arkisto.tutkinto :refer :all]
            [aituhaku.test-util :refer [peridot-session! with-korma]]))

(deftest ^:integraatio hakuehdot
  (with-korma
    #(let [fo (hae-ehdoilla "Vatulointi-jota-ei-ole-olemassa" "fi" nil "fi")]
       (is (empty? fo)))))

(deftest ^:integraatio hakuehdot-on
  (with-korma
    #(let [tutkinto-ei-suorituskielirajausta (hae-ehdoilla "Audio" "fi" nil nil)]
       (is (= 1 (count tutkinto-ei-suorituskielirajausta))))))

(defn pvm-gen [min-pvm max-pvm]
  (let [paivia-valissa (time/in-days (time/interval
                                       (clj-time.coerce/to-date-time min-pvm)
                                       (clj-time.coerce/to-date-time max-pvm)))]
    (gen/fmap #(time/plus min-pvm (time/days %))
              (gen/choose 0 paivia-valissa))))

(def tanaan (time/local-date 2015 1 1))
(def eilen (time/minus tanaan (time/days 1)))
(def huomenna (time/plus tanaan (time/days 1)))
(def _50v-kuluttua (time/plus tanaan (time/years 50)))
(def _50v-sitten (time/minus tanaan (time/years 50)))

(def milloin-tahansa-gen (pvm-gen _50v-sitten _50v-kuluttua))
(def menneisyydessa-gen (pvm-gen _50v-sitten eilen))
(def tulevaisuudessa-gen (pvm-gen huomenna _50v-kuluttua))

(defspec voimassaolo-loppunut-test 2000
  (testing "Tutkinto ei ole voimassa, jos voimassaolon loppupvm on menneisyydessä"
    (prop/for-all [v (gen/hash-map :voimassa_alkupvm milloin-tahansa-gen
                                   :siirtymaajan_loppupvm milloin-tahansa-gen
                                   :voimassa_loppupvm menneisyydessa-gen)]
     (= (voimassaolo tanaan v)
        :ei-voimassa))))

(defspec siirtymaaika-loppunut-test 2000
  (testing "Tutkinto ei ole voimassa, jos siirtymaajan loppupvm on menneisyydessä"
    (prop/for-all [v (gen/hash-map :voimassa_alkupvm milloin-tahansa-gen
                                   :siirtymaajan_loppupvm menneisyydessa-gen
                                   :voimassa_loppupvm milloin-tahansa-gen)]
     (= (voimassaolo tanaan v)
        :ei-voimassa))))

(defspec voimassa-test 2000
  (testing "Tutkinto on voimassa, jos voimassaolon alkupvm on menneisyydessä, voimassaolon loppupvm on tulevaisuudessa ja siirtymäajan loppupvm on time-forever"
    (prop/for-all [v (gen/hash-map :voimassa_alkupvm menneisyydessa-gen
                                   :siirtymaajan_loppupvm (gen/return oph.common.util.util/time-forever)
                                   :voimassa_loppupvm tulevaisuudessa-gen)]
     (= (voimassaolo tanaan v)
        :voimassa))))

(defspec siirtymaajalla-test 2000
  (testing "Tutkinto on siirtymäajalla, jos voimassaolon alkupvm on menneisyydessä, voimassaolon loppupvm on tulevaisuudessa ja siirtymäajan loppupvm on tulevaisuudessa"
    (prop/for-all [v (gen/hash-map :voimassa_alkupvm menneisyydessa-gen
                                   :siirtymaajan_loppupvm tulevaisuudessa-gen
                                   :voimassa_loppupvm tulevaisuudessa-gen)]
     (= (voimassaolo tanaan v)
        :siirtymaajalla))))
