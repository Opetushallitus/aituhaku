(ns aituhaku.rest-api.opintoala
  (:require [compojure.api.core :refer [defroutes GET]]
            [aituhaku.arkisto.opintoala :as arkisto]
            [aituhaku.toimiala.skeema :refer [Koulutusala]]
            [oph.common.util.http-util :refer [response-or-404]]))

(defroutes reitit
  (GET "/haku" []
    :return [Koulutusala]
    (response-or-404 (arkisto/hae))))