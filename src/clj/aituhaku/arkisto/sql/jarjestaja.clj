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

(ns aituhaku.arkisto.sql.jarjestaja
    (:require [korma.core :as sql]
              [aituhaku.arkisto.sql.korma :refer :all]))

(defn hae-oppilaitoskoodit []
  (sql/select tutkinnon_jarjestajat_view
    (sql/fields :oppilaitoskoodi)))
  
(defn hae
  [oppilaitoskoodi]
  (let [jarjestaja (first (sql/select tutkinnon_jarjestajat_view
                            (sql/fields :oppilaitoskoodi :nimi :www_osoite :ktnimi_fi :ktnimi_sv)
                            (sql/where {:oppilaitoskoodi oppilaitoskoodi})))
        tutkinnot (sql/select tutkinnon_jarjestajat_view
                    (sql/join :inner tutkinnot_view
                              (= :jarjestajat.tutkintotunnus :tutkinnot_view.tutkintotunnus))
                    (sql/fields :tutkinnot_view.tutkintotunnus :tutkinnot_view.nimi_fi :tutkinnot_view.nimi_sv)
                    (sql/where {:jarjestajat.oppilaitoskoodi oppilaitoskoodi}))]
    (assoc jarjestaja :tutkinnot tutkinnot)))
