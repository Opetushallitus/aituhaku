(ns aituhaku.arkisto.sql.tutkinto
  (:require [korma.core :as sql])
  (:use [aituhaku.arkisto.sql.korma]))


(defn hae-tutkintojen-tiedot
  []
  (sql/select tutkinnot_view
    (sql/fields :tutkintotunnus :nimi_fi :nimi_sv :opintoala_nimi_fi :opintoala_nimi_sv :tutkintotaso)))

(defn hae
  [tutkintotunnus]
  (sql/select tutkinnot_view
    (sql/with tutkinnon_jarjestajat_view
      (sql/fields :oppilaitoskoodi :oppilaitos_nimi))
    (sql/with tutkinnon_toimikunnat_view
      (sql/fields :nimi_fi :nimi_sv :tkunta))
    (sql/where {:tutkintotunnus tutkintotunnus})))
