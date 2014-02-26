(ns aituhaku.toimiala.skeema
  (:require [schema.core :as s]))

(def Tutkinto {:tutkintotunnus s/Str
               :nimi_fi s/Str
               :nimi_sv s/Str
               :opintoala_nimi_fi s/Str
               :opintoala_nimi_sv s/Str
               (s/optional-key :koulutusala_nimi_fi) s/Str
               (s/optional-key :koulutusala_nimi_sv) s/Str
               :tutkintotaso s/Str})

(def ToimikuntaNimi {:nimi_fi s/Str
                     :nimi_sv s/Str
                     :tkunta s/Str})

(def Toimikunta (merge ToimikuntaNimi
                       {:sahkoposti (s/maybe s/Str)
                        :toimikausi_alku org.joda.time.LocalDate
                        :toimikausi_loppu org.joda.time.LocalDate
                        :kielisyys s/Str}))

(def Jarjestaja {:oppilaitoskoodi s/Str
                 :nimi s/Str})

(def TutkintoTiedot (merge Tutkinto
                           {:jarjestajat [Jarjestaja]
                            :toimikunnat [ToimikuntaNimi]}))
