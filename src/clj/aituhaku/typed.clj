(ns aituhaku.typed
  (:require [clojure.core.typed :as t]
            clj-time.core
            oph.common.util.util)
  (:import org.joda.time.LocalDate))

(t/ann ^:no-check clj-time.core/before? [LocalDate LocalDate -> Boolean])
(t/ann ^:no-check clj-time.core/after? [LocalDate LocalDate -> Boolean])
(t/ann ^:no-check clj-time.core/today [-> LocalDate])
(t/ann ^:no-check clj-time.core/local-date [t/AnyInteger t/AnyInteger t/AnyInteger -> LocalDate])
(t/ann ^:no-check oph.common.util.util/time-forever LocalDate)
(t/ann oph.common.util.util/pvm-menneisyydessa? [LocalDate -> Boolean])
(t/ann oph.common.util.util/pvm-tulevaisuudessa? [LocalDate -> Boolean])
(t/ann oph.common.util.util/pvm-mennyt-tai-tanaan? [LocalDate -> Boolean])
(t/ann oph.common.util.util/pvm-tuleva-tai-tanaan? [LocalDate -> Boolean])
(t/ann ^:no-check clojure.string/lower-case [String -> String])
(t/ann oph.common.util.util/sisaltaako-kentat? (t/All [k] [(t/Map k t/Any) (t/Coll k) String -> Boolean]))
(t/ann oph.common.util.util/some-value-with (t/All [s t] [(t/IFn [s -> t]) t (t/Coll s) -> s]))
