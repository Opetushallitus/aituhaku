(ns aituhaku.arkisto.tutkinto
  (:require [aituhaku.arkisto.sql.tutkinto :as tutkinto-sql]
            [aitu.util :refer [sisaltaako-kentat?]]))

(defn hae-termilla
  [termi]
  (->> (tutkinto-sql/hae-tutkintojen-tiedot)
    (filter #(sisaltaako-kentat? % [:nimi_fi :nimi_sv] termi))))

(defn hae
  [tutkintotunnus]
  (first (tutkinto-sql/hae tutkintotunnus)))
