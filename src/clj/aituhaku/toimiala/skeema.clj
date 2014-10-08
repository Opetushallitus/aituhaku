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

(ns aituhaku.toimiala.skeema
  (:require [schema.core :as s]))

(def TutkintoPerustiedot {:tutkintotunnus s/Str
                          :nimi_fi s/Str
                          :nimi_sv s/Str})

(def Tutkinto (merge TutkintoPerustiedot
                     {:opintoala_nimi_fi s/Str
                      :opintoala_nimi_sv s/Str
                      :opintoala_tkkoodi s/Str
                      (s/optional-key :koulutusala_nimi_fi) s/Str
                      (s/optional-key :koulutusala_nimi_sv) s/Str
                      :tutkintotaso s/Str
                      :voimassa_alkupvm org.joda.time.LocalDate
                      :voimassa_loppupvm org.joda.time.LocalDate
                      :siirtymaajan_loppupvm org.joda.time.LocalDate}))

(def ToimikuntaNimi {:nimi_fi s/Str
                     :nimi_sv s/Str
                     :tkunta s/Str})

(def Jasen {:etunimi s/Str
            :sukunimi s/Str
            :rooli s/Str})

(def Toimikunta (merge ToimikuntaNimi
                       {:sahkoposti (s/maybe s/Str)
                        :toimikausi_alku org.joda.time.LocalDate
                        :toimikausi_loppu org.joda.time.LocalDate
                        :kielisyys s/Str
                        :jasenet [Jasen]}))

(def Jarjestaja {:oppilaitoskoodi s/Str
                 :nimi s/Str
                 :www_osoite (s/maybe s/Str)
                 :ktnimi_fi s/Str
                 :ktnimi_sv s/Str})

(def JarjestajanTutkinnot (merge Jarjestaja
                                 {:tutkinnot [TutkintoPerustiedot]}))

(def TutkintoTiedot (merge Tutkinto
                           {:jarjestajat [Jarjestaja]
                            :toimikunnat [ToimikuntaNimi]
                            :voimassaolo (s/enum :ei-voimassa
                                                 :voimassa
                                                 :siirtymaajalla)}))

(def Opintoala {:opintoala_nimi_fi s/Str
                :opintoala_nimi_sv s/Str
                :opintoala_tkkoodi s/Str
                :voimassa_alkupvm org.joda.time.LocalDate
                :voimassa_loppupvm org.joda.time.LocalDate})

(def Koulutusala {:koulutusala_tkkoodi s/Str
                  :koulutusala_nimi_fi s/Str
                  :koulutusala_nimi_sv s/Str
                  :opintoalat [Opintoala]})
