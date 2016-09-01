(ns aituhaku.rest.toimikunta-test
  (:require
    [aituhaku.test-util :refer [peridot-session! with-korma body-json]]
    [clojure.test :refer :all]    
    [peridot.core :as peridot]))

(deftest ^:integraatio toimikunta-test
  (with-korma
    #(let [peridot-session (peridot-session!)]
      (testing "Toimikuntasivu toimii"
               (let [toimikunta (:response (peridot/request peridot-session "/fi/api/toimikunta/Gulo%20gulo" :request-method :get))
                     respo (body-json toimikunta)]
            (is (= (:status toimikunta) 200)))))))
            
