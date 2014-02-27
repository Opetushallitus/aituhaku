(ns aitu-e2e.http
  "Virheet loggaavat wrapperit clj-http:n funktioille."
  (:refer-clojure :exclude [get])
  (:require [clojure.string :refer [split-lines]]
            [clj-http.client :as hc]))

(defn wrap-poikkeusten-logitus [f]
  (fn [& args]
    (try
      (apply f args)
      (catch clojure.lang.ExceptionInfo e
        ;; Estetään muita säikeitä tulostamasta näiden viestien sekaan.
        (locking System/out
          (locking System/err
            (binding [*out* *err*]
              (println "========== Virhe palvelimella ==========")
              (doseq [line (-> e .getData :object :body split-lines)]
                (println ">" line))
              (println "========================================"))))
        (throw e)))))

(def get (wrap-poikkeusten-logitus hc/get))
(def post (wrap-poikkeusten-logitus hc/post))
(def put (wrap-poikkeusten-logitus hc/put))
(def delete (wrap-poikkeusten-logitus hc/delete))
