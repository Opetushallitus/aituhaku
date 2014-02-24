(ns aituhaku.arkisto.sql.tutkinto
  (:require [korma.core :as sql])
  (:use [aituhaku.arkisto.sql.korma]))


(defn hae-tutkintojen-tiedot
  []
  (sql/select tutkintoview
    (sql/fields :tutkintotunnus :nimi_fi :nimi_sv :opintoala_nimi_fi :opintoala_nimi_sv :tutkintotaso)))
