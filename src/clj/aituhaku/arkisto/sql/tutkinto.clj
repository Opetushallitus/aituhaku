;; Copyright (c) 2013 The Finnish National Board of Education - Opetushallitus
;;
;; This program is free software:  Licensed under the EUPL, Version 1.1 or - as
;; soon as they will be approved by the European Commission - subsequent versions
;; of the EUPL (the "Licence");
;;
;; You may not use this work except in compliance with the Licence.
;; You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
;;
;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; European Union Public Licence for more details.

(ns aituhaku.arkisto.sql.tutkinto
  (:require [korma.core :as sql])
  (:use [aituhaku.arkisto.sql.korma]))


(defn hae-tutkintojen-tiedot
  []
  (sql/select tutkinnot_view
    (sql/fields :tutkintotunnus :nimi_fi :nimi_sv :opintoala_nimi_fi :opintoala_nimi_sv
                :tutkintotaso :voimassa_alkupvm :voimassa_loppupvm :siirtymaajan_loppupvm)))

(defn hae
  [tutkintotunnus]
  (sql/select tutkinnot_view
    (sql/with tutkinnon_jarjestajat_view
      (sql/fields :oppilaitoskoodi :nimi))
    (sql/with tutkinnon_toimikunnat_view
      (sql/fields :nimi_fi :nimi_sv :tkunta))
    (sql/where {:tutkintotunnus tutkintotunnus})))
