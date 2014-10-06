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

(ns aituhaku.infra.i18n
  (:import (java.util Locale
                      ResourceBundle))
  (:require [clojure.string :as s]
            [ring.util.response :refer [redirect]]
            [compojure.core :as c]))

(def ^:dynamic *locale*)

(def suomenkieliset-domainit #{"xn--nytttutkintohaku-vnb14a.fi"
                               "www.xn--nytttutkintohaku-vnb14a.fi"
                               "nayttotutkintohaku.fi"
                               "www.nayttotutkintohaku.fi"
                               "haetutkinto.fi"
                               "www.haetutkinto.fi"})

(def ruotsinkieliset-domainit #{"xn--skexamen-n4a.fi"
                                "www.xn--skexamen-n4a.fi"
                                "sokexamen.fi"
                                "www.sokexamen.fi"})

(defn domainin-kieli
  "Määrittää kielikoodin käytetyn domainin perusteella"
  [request]
  (condp contains? (:server-name request)
    suomenkieliset-domainit "fi"
    ruotsinkieliset-domainit "sv"
    nil))

(defn kielikoodi-ja-uri
  "Pilkkoo annetun URI:n kielikoodiin ja muuhun URI:iin."
  [kysely]
  (rest (re-matches #"(?:/(fi|sv))?(/.*)" (:uri kysely))))

(defn accept-languagen-kielikoodi
  "Palauttaa ensimmäisen tuetun kielikoodin Accept-Language -otsakkeesta."
  [kysely]
  (when-let [accept-language ((:headers kysely) "accept-language")]
    (let [kielet (s/split accept-language #" ")
          kielet-ilman-qta (map #(s/replace % #";.*" "") kielet)]
      (some #{"fi" "sv"} kielet-ilman-qta))))

(defn wrap-locale [ring-handler & {:keys [ei-redirectia, base-url]}]
  (fn [request]
    (let [[urin-kielikoodi uri] (kielikoodi-ja-uri request)
          domainin-kielikoodi (domainin-kieli request)
          accept-languagen-kielikoodi (accept-languagen-kielikoodi request)
          kielikoodi (or urin-kielikoodi accept-languagen-kielikoodi)
          ei-redirectia? (some-> ei-redirectia (re-matches (:uri request)))]
      (if (or kielikoodi ei-redirectia?)
        (with-bindings (if kielikoodi
                         {#'*locale* (Locale. kielikoodi)}
                         {})
          (ring-handler (assoc request :uri uri)))
        (redirect (str base-url "/" (or domainin-kielikoodi "fi") uri))))))
