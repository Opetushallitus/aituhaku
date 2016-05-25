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
  (:require [clojure.core.typed :as t]
            [korma.core :as sql]
            [aituhaku.arkisto.sql.korma :refer :all])
  (:import org.joda.time.LocalDate))

(t/defalias Nimetty (t/HMap :mandatory {:nimi_fi String
                                        :nimi_sv (t/Option String)}))

(t/defalias Toimikunta (t/I Nimetty
                            (t/HMap :mandatory {:tkunta String})))

(t/defalias Jarjestaja (t/HMap :mandatory {:nimi String
                                           :oppilaitoskoodi String
                                           :www_osoite (t/Option String)
                                           :ktnimi_fi String
                                           :ktnimi_sv String}))

(t/defalias TutkinnonVoimassaoloPvm (t/HMap :mandatory {:voimassa_alkupvm LocalDate
                                                        :voimassa_loppupvm LocalDate
                                                        :siirtymaajan_loppupvm LocalDate}))

(t/defalias Tutkintonimike Nimetty)

(t/defalias TutkinnonPerustiedot (t/I TutkinnonVoimassaoloPvm
                                      Nimetty
                                      (t/HMap :mandatory {:tutkintotunnus String
                                                          :tutkintotaso String
                                                          :opintoala_nimi_fi String
                                                          :opintoala_nimi_sv (t/Option String)
                                                          :opintoala_tkkoodi String
                                                          :voimassa_alkupvm LocalDate
                                                          :voimassa_loppupvm LocalDate
                                                          :siirtymaajan_loppupvm LocalDate
                                                          :tutkintonimikkeet (t/Seq Tutkintonimike)})))

(t/defalias Tutkinto (t/I TutkinnonPerustiedot
                          (t/HMap :mandatory {:koulutusala_nimi_fi String
                                              :koulutusala_nimi_sv (t/Option String)
                                              :toimikunnat (t/Seq Toimikunta)
                                              :jarjestajat (t/Seq Jarjestaja)})))

(t/ann ^:no-check hae-tutkintojen-tiedot [String String -> (t/Seq TutkinnonPerustiedot)])
(defn hae-tutkintojen-tiedot [opintoala suorituskieli]
  {:post [((t/pred (t/Seq TutkinnonPerustiedot)) %)]}
  (let [tutkinnot (sql/select tutkinnot_view
                    (sql/fields :tutkintotunnus :nimi_fi :nimi_sv :opintoala_nimi_fi :opintoala_nimi_sv :opintoala_tkkoodi
                                :tutkintotaso :voimassa_alkupvm :voimassa_loppupvm :siirtymaajan_loppupvm)
                    (sql/where (or
                                 (nil? opintoala)
                                 {:opintoala_tkkoodi opintoala}))
                    (sql/where (or
                                 (nil? suorituskieli)
                                 (exists (sql/subselect tutkinnon_jarjestajat_view
                                           (sql/where {:tutkintotunnus :tutkinnot_view.tutkintotunnus
                                                       :kieli suorituskieli}))))))
        nimikkeet (group-by :tutkintotunnus (sql/select tutkintonimike_view))]
    (for [tutkinto tutkinnot
          :let [nimikkeet (map #(dissoc % :tutkintotunnus) (get nimikkeet (:tutkintotunnus tutkinto)))]]
      (assoc tutkinto :tutkintonimikkeet nimikkeet))))

(t/ann ^:no-check hae [String -> (t/Seq Tutkinto)])
(defn hae [tutkintotunnus]
  {:post [((t/pred (t/Seq Tutkinto)) %)]}
  (sql/select tutkinnot_view
    (sql/fields :tutkintotunnus :nimi_fi :nimi_sv :tutkintotaso
                :opintoala_nimi_fi :opintoala_nimi_sv :koulutusala_nimi_fi :koulutusala_nimi_sv
                :voimassa_alkupvm :voimassa_loppupvm :siirtymaajan_loppupvm
                :opintoala_tkkoodi :peruste :eperustetunnus)
    (sql/with tutkinnon_jarjestajat_view
      (sql/fields :oppilaitoskoodi :nimi :www_osoite :ktnimi_fi :ktnimi_sv :kieli))
    (sql/with tutkinnon_toimikunnat_view
      (sql/fields :nimi_fi :nimi_sv :tkunta))
    (sql/with tutkintonimike_view
      (sql/fields :nimi_fi :nimi_sv))
    (sql/where {:tutkintotunnus tutkintotunnus})
    (sql/order :voimassa_alkupvm :desc)))
