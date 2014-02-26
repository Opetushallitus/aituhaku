(ns aituhaku.rest-api.tutkinto
  (:require [compojure.core :as c]
            [schema.core :as schema]
            [aitu.rest-api.http-util :refer [cachable-json-response json-response]]
            [aituhaku.toimiala.skeema :refer [Tutkinto TutkintoTiedot]]
            [aituhaku.arkisto.tutkinto :as arkisto]))

(c/defroutes reitit
  (c/GET "/haku" [termi :as req]
    (schema/validate schema/Str termi)
    (cachable-json-response req
                            (arkisto/hae-termilla termi)
                            [Tutkinto]))

  (c/GET "/:tutkintotunnus" [tutkintotunnus :as req]
    (schema/validate schema/Str tutkintotunnus)
    (cachable-json-response req
                            (arkisto/hae tutkintotunnus)
                            TutkintoTiedot)))
