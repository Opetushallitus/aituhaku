(ns aituhaku.arkisto.opintoala
  (:require [aituhaku.arkisto.sql.opintoala :as opintoala-sql]
            [aitu.util :refer [sisaltaako-kentat?]]
            [aitu.timeutil :as timeutil]))

(defn opintoala-voimassa?
  [opintoala]
  (and (timeutil/pvm-mennyt-tai-tanaan? (:voimassa_alkupvm opintoala))
       (timeutil/pvm-tuleva-tai-tanaan? (:voimassa_loppupvm opintoala))))

(defn poista-ylimaaraiset
  [opintoala]
  (dissoc opintoala :koulutusala_tkkoodi :koulutusala_nimi_fi :koulutusala_nimi_sv))

(defn hae-termilla
  "Hakee opintoalan kentista termillä."
  [termi kieli]
  (let [opintoalat (->> (opintoala-sql/hae kieli)
                        (filter opintoala-voimassa?)
                        (filter #(sisaltaako-kentat? % [(keyword (str "opintoala_nimi_" kieli))] termi)))
        opintoalat_koulutusaloittain (group-by #(select-keys % [:koulutusala_tkkoodi :koulutusala_nimi_fi :koulutusala_nimi_sv]) opintoalat)
        koulutusalat (keys opintoalat_koulutusaloittain)]
    (sort-by :koulutusala_tkkoodi (map #(assoc % :opintoalat (map poista-ylimaaraiset (get opintoalat_koulutusaloittain %))) koulutusalat))))
