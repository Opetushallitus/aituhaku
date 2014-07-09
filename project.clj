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

(defproject aituhaku "0.1.0-SNAPSHOT"
  :description "Aituhaku"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring/ring-core "1.2.1"]
                 [http-kit "2.1.10"]
                 [compojure "1.1.5"]
                 [ring/ring-json "0.2.0"]
                 [cheshire "5.2.0"]
                 [stencil "0.3.3"]
                 [org.clojure/tools.logging "0.2.6"]
                 [ch.qos.logback/logback-classic "1.0.13"]
                 [org.slf4j/slf4j-api "1.7.5"]
                 [clj-time "0.6.0"]
                 [com.cemerick/valip "0.3.2"]
                 [prismatic/schema "0.2.0"]
                 [korma "0.3.0-RC6"]
                 [postgresql "9.1-901.jdbc4"]
                 [stencil "0.3.2"]
                 [org.clojure/core.typed "0.2.53"]]
  :plugins [[test2junit "1.0.1"]
            [lein-typed "0.3.4"]]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.4"]
                                  [org.clojure/test.check "0.5.8"]]}
             :uberjar {:main aituhaku.palvelin
                       :aot :all}}
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :main aituhaku.palvelin
  :repl-options {:init-ns user}
  :jar-name "aituhaku.jar"
  :uberjar-name "aituhaku-standalone.jar"
  :core.typed {:check [aituhaku.arkisto.tutkinto]})
