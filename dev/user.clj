(ns user
  (:require [clojure.repl :refer :all]
            [clojure.pprint :refer [pprint]]
            [clojure.tools.namespace.repl :as nsr]
            [clj-http.client :as hc]))

(defonce ^:private palvelin (atom nil))

(defn ^:private kaynnista! []
  {:pre [(not @palvelin)]
   :post [@palvelin]}
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
