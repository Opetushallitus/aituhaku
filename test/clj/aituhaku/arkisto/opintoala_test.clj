(ns aituhaku.arkisto.opintoala-test
  (:require 
    [clojure.test :refer :all]
    [aituhaku.arkisto.opintoala :refer :all]
    [aituhaku.test-util :refer [peridot-session! with-korma]]))

(deftest ^:integraatio hakuehdot
  (with-korma
    #(let [fo (hae)]
       (is (= 1 (count fo)))
       (is (= (:koulutusala_tkkoodi (first fo)) "6")))))         
