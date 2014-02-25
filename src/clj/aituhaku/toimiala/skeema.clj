(ns aituhaku.toimiala.skeema
  (:require [schema.core :as s]))

(def Tutkinto {:tutkintotunnus s/Str
               :nimi_fi s/Str
               :nimi_sv s/Str
               :opintoala_nimi_fi s/Str
               :opintoala_nimi_sv s/Str
               :tutkintotaso s/Str})

(def Toimikunta {:tkunta s/Str
                 :nimi_fi s/Str
                 :nimi_sv s/Str
                 :sahkoposti (s/maybe s/Str)
                 :toimikausi_alku org.joda.time.LocalDate
                 :toimikausi_loppu org.joda.time.LocalDate
                 :kielisyys s/Str})
