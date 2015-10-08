(ns aituhaku.arkisto.opintoala
  (:require [aituhaku.arkisto.sql.opintoala :as opintoala-sql]
            [oph.common.util.util :refer [sisaltaako-kentat? pvm-mennyt-tai-tanaan? pvm-tuleva-tai-tanaan?]]))

(defn opintoala-voimassa?
  [opintoala]
  (and (pvm-mennyt-tai-tanaan? (:voimassa_alkupvm opintoala))
       (pvm-tuleva-tai-tanaan? (:voimassa_loppupvm opintoala))))

(defn poista-ylimaaraiset
  [opintoala]
  (dissoc opintoala :koulutusala_tkkoodi :koulutusala_nimi_fi :koulutusala_nimi_sv))

(defn hae
  "Hakee koulutusalat ja niiden opintoalat"
  []
  (let [opintoalat (filter opintoala-voimassa? (opintoala-sql/hae))
        opintoalat-koulutusaloittain (group-by #(select-keys % [:koulutusala_tkkoodi :koulutusala_nimi_fi :koulutusala_nimi_sv]) opintoalat)]
    (sort-by :koulutusala_tkkoodi (for [[koulutusala opintoalat] opintoalat-koulutusaloittain]
                                    (assoc koulutusala :opintoalat (map poista-ylimaaraiset opintoalat))))))
