(defproject aituhaku-e2e "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-webdriver "0.6.0"]
                 [clj-http "0.7.6"]
                 [cheshire "5.2.0"]
                 [com.paulhammant/ngwebdriver "0.9.1"]
                 [clj-time "0.6.0"]]
  :plugins [[test2junit "1.0.1"]])

(require '[robert.hooke :refer [add-hook]])
(require 'leiningen.test)

(add-hook #'leiningen.test/form-for-testing-namespaces
          (fn [f & args]
            (binding [leiningen.test/*exit-after-tests* false]
              (let [form (apply f args)]
                `(do
                   (require 'aitu-e2e.util)
                   (System/exit (aitu-e2e.util/with-webdriver ~form)))))))
