(ns aituhaku.arkisto.sql.toimikunta
  (:require [korma.core :as sql]
            [aituhaku.arkisto.sql.korma :refer :all]))

(defn hae
  [tkunta]
  (first (sql/select toimikunta_view
           (sql/where {:tkunta tkunta}))))
