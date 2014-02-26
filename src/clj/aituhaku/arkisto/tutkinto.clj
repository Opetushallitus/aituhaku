(ns aituhaku.arkisto.tutkinto
  (:require [aituhaku.arkisto.sql.tutkinto :as tutkinto-sql]
            [aitu.util :refer [sisaltaako-kentat?]]
            [aitu.timeutil :as timeutil]))

(defn tutkinto-voimassa?
  [tutkinto]
  (and (timeutil/pvm-mennyt-tai-tanaan? (:voimassa_alkupvm tutkinto))
       (timeutil/pvm-tuleva-tai-tanaan? (:siirtymaajan_loppupvm tutkinto))))

(defn hae-termilla
  [termi]
  (->> (tutkinto-sql/hae-tutkintojen-tiedot)
    (filter tutkinto-voimassa?)
    (filter #(sisaltaako-kentat? % [:nimi_fi :nimi_sv] termi))))

(defn hae
  [tutkintotunnus]
  (first (tutkinto-sql/hae tutkintotunnus)))

