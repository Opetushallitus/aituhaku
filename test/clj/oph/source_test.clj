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

(ns oph.source-test
  "Tarkistuksia lähdekoodille."
  (:import java.io.PushbackReader)
  (:require [clojure.test :refer [deftest testing is]]
            [oph.source-util :refer :all]))

(deftest js-debug-test
  (is (empty? (js-console-log-calls))))

(deftest pre-post-oikeassa-paikassa-test
  (is (empty? (vastaavat-muodot "src/clj" pre-post-vaarassa-paikassa?))))

(deftest pre-post-vektori-test
  (is (empty? (vastaavat-muodot "src/clj" pre-post-ei-vektori?))))

