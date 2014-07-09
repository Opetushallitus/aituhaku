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

(ns user
  (:require [clojure.repl :refer :all]
            [clojure.pprint :refer [pprint]]
            [clojure.java.shell :refer [with-sh-dir sh]]
            [clojure.tools.namespace.repl :as nsr]
            clojure.core.cache
            schema.core
            stencil.loader))

(schema.core/set-fn-validation! true)

(defonce ^:private palvelin (atom nil))

;; Templatejen kakutus pois päältä kehityksen aikana
(stencil.loader/set-cache (clojure.core.cache/ttl-cache-factory {} :ttl 0))

(def frontend-kaannoskomennot ["npm install"
                               "bower install"
                               "grunt build"])

(defn kaanna-frontend []
  (with-sh-dir "frontend"
    (doseq [komento frontend-kaannoskomennot]
      (println "$" komento)
      (let [{:keys [err out]} (sh "bash" "-l" "-c" komento)]
        (println err out)))))

(defn ^:private kaynnista! []
  {:pre [(not @palvelin)]
   :post [@palvelin]}
  (kaanna-frontend)
  (require 'aituhaku.palvelin)
  (reset! palvelin ((ns-resolve 'aituhaku.palvelin 'kaynnista!)
                     (assoc @(ns-resolve 'aituhaku.asetukset 'oletusasetukset)
                            :development-mode true))))

(defn ^:private sammuta! []
  {:pre [@palvelin]
   :post [(not @palvelin)]}
  ((ns-resolve 'aituhaku.palvelin 'sammuta) @palvelin)
  (reset! palvelin nil))

(defn uudelleenkaynnista! []
  (when @palvelin
    (sammuta!))
  (nsr/refresh :after 'user/kaynnista!))
