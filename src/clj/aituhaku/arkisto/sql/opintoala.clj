(ns aituhaku.arkisto.sql.opintoala
  (:require [korma.core :as sql]
            [aituhaku.arkisto.sql.korma :refer :all]))

(defn hae
  []
  (sql/select opintoala_view))
