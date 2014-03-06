(ns aituhaku.rest-api.tutkinto
  (:require [compojure.core :as c]
            [schema.core :as schema]
            [aitu.rest-api.http-util :refer [cachable-json-response json-response]]
            [aituhaku.toimiala.skeema :refer [Tutkinto TutkintoTiedot]]
            [aituhaku.arkisto.tutkinto :as arkisto]))

(c/defroutes reitit
  (c/GET "/haku" [nimi opintoala :as req]
    (schema/validate (schema/maybe schema/Str) nimi)
    (schema/validate (schema/maybe schema/Str) opintoala)
    (cachable-json-response req
      (arkisto/hae-ehdoilla [{:hakuehto nimi
                              :kentat [:nimi_fi :nimi_sv]}
                             {:hakuehto opintoala
                              :kentat [:opintoala_nimi_fi
                                       :opintoala_nimi_sv]}])
      [Tutkinto]))

  (c/GET "/:tutkintotunnus" [tutkintotunnus :as req]
    (schema/validate schema/Str tutkintotunnus)
    (cachable-json-response req
                            (arkisto/hae tutkintotunnus)
                            TutkintoTiedot)))
