(ns aituhaku.rest.toimikuntalista-test
   (:require
     [aituhaku.test-util :refer [peridot-session! with-korma body-json]]
     [clojure.test :refer :all]    
     [peridot.core :as peridot]))

(deftest ^:integraatio toimikuntalista-test
  (with-korma
    #(let [peridot-session (peridot-session!)]
      (testing "Toimikuntalista ja rajapinta toimii"
               (let [lista (:response (peridot/request peridot-session "/fi/api/toimikunta" :request-method :get))
                     
                     lista-respo (body-json lista)]
            (is (= (:status lista) 200))
            (is (= '("Aavasaksalainen testitoimikunta" "Lattaraudan taivutuksen testitoimikunta") (map :nimi_fi lista-respo))))))))
