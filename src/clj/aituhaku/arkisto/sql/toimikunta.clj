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

(ns aituhaku.arkisto.sql.toimikunta
  (:require [korma.core :as sql]
            [aituhaku.arkisto.sql.korma :refer :all]))

(defn hae
  [tkunta]
  ; TODO: select-unique
  (let [toimikunta (first (sql/select toimikunta_view
                            (sql/where {:tkunta tkunta})))
        jasenet (sql/select toimikuntien_jasenet_view
                  (sql/fields :etunimi :sukunimi :rooli)
                  (sql/where {:toimikunta tkunta})
                  ; OPH-1835: puheenjohtaja, varapuheenjohtaja, jäsenet, pysyvät asiantuntijat, tutkintotoimikunnan sihteeri
                  (sql/order (sql/raw "case rooli when 'puheenjohtaja' then 1 when 'varapuheenjohtaja' then 2 when 'jasen' then 3 when 'asiantuntija' then 4 else 5 end, sukunimi, etunimi")))
        tutkinnot (sql/select :aituhaku.tutkinnon_toimikunnat_view
                    (sql/fields :tutkintotunnus 
                                [:tutkinto_nimi_fi :nimi_fi]
                                [:tutkinto_nimi_sv :nimi_sv]
                                [(sql/sqlfn exists (sql/subselect :aituhaku.tutkinnon_jarjestajat_view (sql/where (= :aituhaku.tutkinnon_toimikunnat_view.tutkintotunnus :aituhaku.tutkinnon_jarjestajat_view.tutkintotunnus)))) :sopimuksia ])
                    (sql/where {:tkunta tkunta}))]
    (-> toimikunta
      (assoc :tutkinnot tutkinnot) 
      (assoc :jasenet jasenet))))

(defn hae-kaikki []
  (let [soppareita (set (sql/select :aituhaku.tutkinnon_jarjestajat_view
                           (sql/fields :tutkintotunnus)
                           (sql/modifier "DISTINCT")))]

    (let [sopimuksia (fn [x] (map #(assoc % :sopimuksia (contains? soppareita {:tutkintotunnus (:tutkintotunnus %)})) x))
          lista (map #(clojure.set/rename-keys % {:toimikunnat :tutkinnot})
                     (sql/select toimikunta_view
                                 (sql/with tutkinnon_toimikunnat_view
                                   (sql/fields :tutkintotunnus 
                                               [:tutkinto_nimi_fi :nimi_fi]
                                               [:tutkinto_nimi_sv :nimi_sv]))
                                 (sql/order :nimi_fi)))
        lista2 (map #(assoc % :tutkinnot (sopimuksia (:tutkinnot %))) lista)]
        lista2)))

