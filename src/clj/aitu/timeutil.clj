(ns aitu.timeutil
  (:require [clj-time.core :as ctime]
            [clj-time.coerce :as tcoerce]))

; työn alla..
; mutta tarkoitus on kerätä tänne kamaa, jolla voidaan käsitellä versiointia ja voimassaoloa järkevästi
(def time-forever (ctime/local-date 2199 1 1))

(defn pvm-menneisyydessa?
  [pvm]
  {:pre [(not (nil? pvm))]}
  (let [nytpvm (ctime/today)]
    (ctime/after? nytpvm pvm)))


(defn pvm-tulevaisuudessa?
  [pvm]
  {:pre [(not (nil? pvm))]}
  (let [nytpvm (ctime/today)]
    (ctime/before? nytpvm pvm)))

(def pvm-mennyt-tai-tanaan?
  (complement pvm-tulevaisuudessa?))

(def pvm-tuleva-tai-tanaan?
  (complement pvm-menneisyydessa?))
