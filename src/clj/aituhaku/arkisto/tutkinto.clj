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

(ns aituhaku.arkisto.tutkinto
  (:require [clojure.core.typed :as t]
            [clj-time.core :as time]
            [aituhaku.arkisto.sql.tutkinto :as tutkinto-sql
             :refer [TutkinnonPerustiedot Tutkinto Nimetty]]
            [oph.common.util.util :refer [sisaltaako-kentat? pvm-mennyt-tai-tanaan? pvm-tuleva-tai-tanaan? time-forever]]
            aituhaku.typed)
  (:import org.joda.time.LocalDate))

(t/ann tutkinto-voimassa? [TutkinnonPerustiedot -> Boolean])
(defn tutkinto-voimassa?
  [tutkinto]
  (and (pvm-mennyt-tai-tanaan? (:voimassa_alkupvm tutkinto))
       (pvm-tuleva-tai-tanaan? (:voimassa_loppupvm tutkinto))))

(t/defalias TutkinnonVoimassaolo (t/U ':voimassa ':ei-voimassa ':siirtymaajalla))

(t/ann voimassaolo [LocalDate tutkinto-sql/TutkinnonVoimassaoloPvm -> TutkinnonVoimassaolo])
(defn voimassaolo [ajankohta voimassaolo-pvm]
  (let [{:keys [voimassa_alkupvm siirtymaajan_loppupvm voimassa_loppupvm]}
        voimassaolo-pvm]
    (if (time/before? siirtymaajan_loppupvm ajankohta)
      :ei-voimassa
      (if (and (time/before? voimassa_alkupvm ajankohta)
               (time/after? voimassa_loppupvm ajankohta))
        (if (and (not= siirtymaajan_loppupvm time-forever)
                 (time/after? siirtymaajan_loppupvm ajankohta))
          :siirtymaajalla
          :voimassa)
        :ei-voimassa))))

(t/ann sisaltaako-nimi? [String String Nimetty -> Boolean])
(defn sisaltaako-nimi?
  [nimi kieli entity]
  (let [nimikentta (case kieli
                     "fi" :nimi_fi
                     "sv" :nimi_sv)]
    (sisaltaako-kentat? entity [nimikentta] nimi)))

(t/ann sisaltaako-nimi-tai-nimike? [String String TutkinnonPerustiedot -> Boolean])
(defn sisaltaako-nimi-tai-nimike?
  [nimi kieli tutkinto]
  (boolean (or (sisaltaako-nimi? nimi kieli tutkinto)
               (some (partial sisaltaako-nimi? nimi kieli) (:tutkintonimikkeet tutkinto)))))

(t/ann hae-ehdoilla [String String String String -> (t/Seq TutkinnonPerustiedot)])
(defn hae-ehdoilla
  "Hakee kentistä ehdoilla."
  [nimi kieli opintoala suorituskieli]
  (let [kieli-rajaus (or suorituskieli kieli)] ; kieli-rajaus on suorituskieli, jos se on annettu. Muussa tapauksessa käyttökieli.
    (->> (tutkinto-sql/hae-tutkintojen-tiedot opintoala suorituskieli)
      (filter tutkinto-voimassa?)
      (filter (partial sisaltaako-nimi-tai-nimike? nimi kieli-rajaus)))))

(t/defalias TutkintoJaVoimassaolo
  (t/I Tutkinto
       (t/HMap :mandatory {:voimassaolo TutkinnonVoimassaolo})))

(t/ann hae [String -> (t/Option TutkintoJaVoimassaolo)])
(defn hae [tutkintotunnus]
  (when-let [tutkinto (first (tutkinto-sql/hae tutkintotunnus))]
    (assoc tutkinto :voimassaolo (voimassaolo (time/today) tutkinto))))
