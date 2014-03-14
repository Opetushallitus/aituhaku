(ns aituhaku.arkisto.opintoala
  (:require [aituhaku.arkisto.sql.opintoala :as opintoala-sql]
            [aitu.util :refer [sisaltaako-kentat?]]
            [aitu.timeutil :as timeutil]))

(defn opintoala-voimassa?
  [opintoala]
  (and (timeutil/pvm-mennyt-tai-tanaan? (:voimassa_alkupvm opintoala))
       (timeutil/pvm-tuleva-tai-tanaan? (:voimassa_loppupvm opintoala))))

(defn hae-termilla
  "Hakee opintoalan nimestä termillä."
  [termi]
  (->> (opintoala-sql/hae)
      (filter opintoala-voimassa?)
      (filter #(sisaltaako-kentat? % [:opintoala_nimi_fi :opintoala_nimi_sv] termi))))