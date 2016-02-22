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

(ns aituhaku-e2e.util
  (:require [clj-webdriver.taxi :as w]
            [aitu-e2e.util :refer [avaa-url]]
            [aitu-e2e.datatehdas :refer [setup-voimassaoleva-jarjestamissopimus merge-datamaps]]))

(defn aituhaku-url [polku]
  (str (or (System/getenv "AITUHAKU_URL")
           "http://localhost:8081")
       polku))

(defn avaa [polku]
  (avaa-url (aituhaku-url polku)))

(defn luo-sopimukset-tutkinnoille [testidata]
  (let [koulutustoimija (-> (:koulutustoimijat testidata) first :ytunnus)
        oppilaitos (-> (:oppilaitokset testidata) first :oppilaitoskoodi)
        tkunta (-> (:toimikunnat testidata) first :tkunta)
        tutkinnot (map #(assoc %1 :tutkintoversio_id %2) (:tutkinnot testidata) (iterate dec -1))
        {:keys [jarjestamissopimukset sopimus_ja_tutkinto]} (apply merge-datamaps
                                                                   (for [tutkinto tutkinnot]
                                                                     (setup-voimassaoleva-jarjestamissopimus koulutustoimija
                                                                                                             oppilaitos
                                                                                                             tkunta
                                                                                                             (:tutkintoversio_id tutkinto))))]
    (->
      testidata
      (assoc :tutkinnot tutkinnot)
      (update-in [:jarjestamissopimukset] concat jarjestamissopimukset)
      (update-in [:sopimus_ja_tutkinto] concat sopimus_ja_tutkinto))))

