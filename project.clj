(defproject aituhaku "0.1.0-SNAPSHOT"
  :description "Aituhaku"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring/ring-core "1.2.1"]
                 [http-kit "2.1.10"]
                 [compojure "1.1.5"]
                 [ring/ring-json "0.2.0"]
                 [cheshire "5.2.0"]
                 [stencil "0.3.3"]
                 [org.clojure/core.cache "0.6.2"]]
  :plugins [[test2junit "1.0.1"]]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.4"]
                                  [clj-webdriver "0.6.0"]
                                  [clj-http "0.7.6"]
                                  [ring-mock "0.1.5"]]}
             :uberjar {:main aituhaku.palvelin
                       :aot :all}}
  :source-paths ["src/clj"]
  :main aituhaku.palvelin)
