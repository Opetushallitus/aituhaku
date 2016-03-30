(ns aituhaku.rest.domain-redirect-test
   (:require
     [aituhaku.test-util :refer [peridot-session!]]
     [clojure.test :refer :all]    
     [peridot.core :as peridot]))

(deftest ^:integraatio domain-redirect-test
  (let [peridot-session (peridot-session!)]
    (testing "Aituhaun kieli riippuu käytetystä domainista"
      (are [domain kieli] (let [url (str "http://" domain)
                                response (:response (peridot/request peridot-session url))]
                            (and (= (:status response) 302)
                                 (= (get-in response [:headers "Location"]) (str "/" kieli "/"))))
           "nayttotutkintohaku.fi" "fi"
           "sokexamen.fi" "sv")
      (testing "Jos domain ei ole mikään määritellyistä, kieli on suomi"
        (let [url "http://maarittelematon.url"
              response (:response (peridot/request peridot-session url))]
          (is (= (:status response) 302))
          (is (= (get-in response [:headers "Location"]) "/fi/")))))))
