(ns aitu-e2e.datatehdas
  (:require
    [aitu-e2e.data-util :refer [menneisyydessa tulevaisuudessa]]))

(defn tunnusgeneraattori
  ([f]
    (let [a (atom 0)]
      (fn []
        (let [id (swap! a inc)]
          (f id)))))
  ([] (tunnusgeneraattori identity)))

(def uusi-sopimusnumero! (tunnusgeneraattori str))
(def uusi-oppilaitostunnus! (tunnusgeneraattori str))
(def uusi-toimikuntatunnus! (tunnusgeneraattori #(str "TTK" %)))

(defn setup-oppilaitos
  ([koodi nimi]
    {:oppilaitoskoodi koodi
     :nimi nimi})
  ([koodi]
    (setup-oppilaitos koodi "Ruikonperän multakurkkuopisto"))
  ([] (setup-oppilaitos (uusi-oppilaitostunnus!))))

(defn oppilaitos-nimella [nimi]
  (setup-oppilaitos (uusi-oppilaitostunnus!) nimi))

(defn setup-opintoala
  [koodi nimi]
  {:koodi koodi
   :selite_fi nimi
   :koulutusala "KA1"})

(defn setup-tutkinto-map
  ([tunnus versio]
  {
   :koulutusalat {:koodi "KA1"}
   :opintoalat (setup-opintoala "OPI" "Ilmausala")
   :tutkinnot {:nimi_fi "Ilmastointialan tutkinto"
               :tutkintotunnus tunnus
               :versio versio
               :tutkintoversio_id versio
               :koodistoversio versio
               :opintoala "OPI"}
   })
  ([] (setup-tutkinto-map "TU1" 1)))

(defn tutkinto-opintoalan-nimella
  [opintoala-nimi]
  (assoc (setup-tutkinto-map) :opintoalat
    (setup-opintoala "OPI" opintoala-nimi)))

(defn setup-voimassaoleva-jarjestamissopimus
  ([sopimusnumero oppilaitos toimikunta tutkintoversio]
    (let [jarjestamissopimusid (Integer/parseInt sopimusnumero)
          oppilaitostunnus (get oppilaitos :oppilaitoskoodi oppilaitos)
          toimikuntatunnus (get toimikunta :tkunta toimikunta)
          tutkintoversio-id (get tutkintoversio :tutkintoversio_id tutkintoversio)]
      {:jarjestamissopimukset
        {:toimikunta toimikuntatunnus
         :sopijatoimikunta toimikuntatunnus
         :oppilaitos oppilaitostunnus
         :jarjestamissopimusid jarjestamissopimusid
         :sopimusnumero sopimusnumero
         :alkupvm menneisyydessa
         :loppupvm tulevaisuudessa}
       :sopimus_ja_tutkinto
       {:jarjestamissopimusid jarjestamissopimusid
        :sopimus_ja_tutkinto [{:tutkintoversio_id tutkintoversio-id}]}}))
  ([oppilaitostunnus toimikuntatunnus tutkintoversio]
    (setup-voimassaoleva-jarjestamissopimus (uusi-sopimusnumero!) oppilaitostunnus toimikuntatunnus tutkintoversio)))

(defn setup-lakannut-jarjestamissopimus [sopimusnumero oppilaitostunnus toimikuntatunnus tutkintoversio]
  (assoc-in
    (setup-voimassaoleva-jarjestamissopimus sopimusnumero oppilaitostunnus toimikuntatunnus tutkintoversio)
    [:jarjestamissopimukset :loppupvm] menneisyydessa))


(defn setup-toimikunta
  ([toimikuntatunnus]
  {:toimikunnat [{:tkunta toimikuntatunnus}]})
  ([] (setup-toimikunta "ILMA")))

(defn toimikunta-nimella [nimi]
  {:nimi_fi nimi
   :tkunta (uusi-toimikuntatunnus!)
   :toimikausi 2})

(defn toimikunta-diaarinumerolla [diaarinumero tkunta]
  (assoc
    (toimikunta-nimella "Hevoshieronnan toimikunta")
    :diaarinumero diaarinumero
    :tkunta tkunta))

(defn toimikunta-nimella-vastuussa-tutkinnosta [nimi tutkintotunnus]
  (let [toimikunta (toimikunta-nimella nimi)]
    {:toimikunnat toimikunta
     :toimikunta_ja_tutkinto {:toimikunta (:tkunta toimikunta)
                              :tutkintotunnus tutkintotunnus}}))

(defn vanha-toimikunta-nimella-vastuussa-tutkinnosta [nimi tutkintotunnus]
  (let [t (toimikunta-nimella-vastuussa-tutkinnosta nimi tutkintotunnus)
        toimikunta (:toimikunnat t)]
    (assoc t :toimikunnat
      (merge toimikunta
        {:toimikausi 1
        :toimikausi_alku "2010-08-01"}))))

(defn jarjesto-nimella
  [jarjesto-nimi]
  {:jarjestoid ((tunnusgeneraattori))
   :nimi_fi jarjesto-nimi
   :nimi_sv jarjesto-nimi})

(defn henkilo-nimella
  [etunimi sukunimi]
  {:henkiloid ((tunnusgeneraattori))
   :etunimi etunimi
   :sukunimi sukunimi})

(defn henkilo-jarjestolla
  [etunimi sukunimi jarjestoid]
  (let [henkilo (henkilo-nimella etunimi sukunimi)]
    (assoc henkilo :jarjesto jarjestoid)))

(defn vectorize [m]
  (into {} (for [[k v] m]
             {k (cond
                  (vector? v) v
                  (seq? v) (vec v)
                  :else (vector v))})))

(defn merge-datamaps
  [& ms]
    (vectorize (apply merge-with concat (map vectorize ms))))

(defn luo-tutkintoja-opintoalaan [lkm opintoala]
  (take lkm
        (for [a (seq "abcdefghijklmnopqrstuwvxyz")
              i (range 9)]
          {:nimi_fi (str "Haettava tutkinto " a (+ i 1 ))
           :tutkintotunnus (str "TU" a (+ i 1 ) )
           :opintoala opintoala})))

(defn tutkinnot-oletus-testidata []
  (let [toimikunnat (assoc-in (setup-toimikunta) [:toimikunnat 0 :nimi_fi] "Tutkinnon toimikunnan nimi")
        tutkinnot (luo-tutkintoja-opintoalaan 25 "OA1")
        sopimuksen_oppilaitos (setup-oppilaitos)
        sopimuksen_toimikunta (:tkunta (get-in toimikunnat [:toimikunnat 0]))
        toimikunnan_tutkinto (get-in (vec tutkinnot) [0 :tutkintotunnus])
        sopimus (setup-voimassaoleva-jarjestamissopimus "12345" (:oppilaitoskoodi sopimuksen_oppilaitos) sopimuksen_toimikunta -1)
        muu-testidata {:koulutusalat [{:koodi "KA1"
                                       :selite_fi "Koulutusalan nimi"}]
                       :opintoalat [{:koodi "OA1"
                                     :koulutusala "KA1"
                                     :selite_fi "Opintoalan nimi"}]
                       :tutkinnot tutkinnot
                       :toimikunta_ja_tutkinto [{:toimikunta sopimuksen_toimikunta
                                                 :tutkintotunnus toimikunnan_tutkinto}]}]
    (merge
      muu-testidata
      toimikunnat
      {:oppilaitokset [sopimuksen_oppilaitos]}
      (->
        (update-in sopimus [:jarjestamissopimukset] (partial conj []))
        (update-in [:sopimus_ja_tutkinto] (partial conj []))))))
