(ns aituhaku.arkisto.sql.opintoala
  (:require [korma.core :as sql]
            [aituhaku.arkisto.sql.korma :refer :all]))

(defn hae
  [kieli]
  (if (= "fi" kieli)
    (sql/select opintoala_view
      (sql/order :koulutusala_tkkoodi :asc, :opintoala_nimi_fi :asc))
    (sql/select opintoala_view
      (sql/order :koulutusala_tkkoodi :asc, :opintoala_nimi_sv :asc))))
