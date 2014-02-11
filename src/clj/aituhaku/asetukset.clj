(ns aituhaku.asetukset
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :refer [file]])
  (:import [ch.qos.logback.classic.joran JoranConfigurator]
           [org.slf4j LoggerFactory]))

(def oletusasetukset
  {:server {:port 8080}
   :logback {:properties-file "resources/logback.xml"}})

(defn konfiguroi-lokitus
  "Konfiguroidaan logback asetukset tiedostosta."
  [asetukset]
  (let [filepath (-> asetukset :logback :properties-file)
        config-file (file filepath)
        config-file-path (.getAbsolutePath config-file)
        configurator (JoranConfigurator.)
        context (LoggerFactory/getILoggerFactory)]
    (log/info "logback configuration reset: " config-file-path)
    (.setContext configurator context)
    (.reset context)
    (.doConfigure configurator config-file-path)))
