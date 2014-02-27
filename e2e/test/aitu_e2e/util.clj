(ns aitu-e2e.util
  (:import com.paulhammant.ngwebdriver.ByAngular
           com.paulhammant.ngwebdriver.WaitForAngularRequestsToFinish
           java.util.concurrent.TimeUnit
           (org.openqa.selenium.remote CapabilityType
                                       DesiredCapabilities)
           (org.openqa.selenium UnexpectedAlertBehaviour
                                NoAlertPresentException
                                UnhandledAlertException)
           org.openqa.selenium.firefox.FirefoxDriver
           org.openqa.selenium.TimeoutException)
  (:require [clojure.test :refer [is]]
            [clj-webdriver.taxi :as w]
            [clj-webdriver.driver :refer [init-driver]]))

(def ^:dynamic *ng*)

(defn odota-sivun-latautumista []
  (w/wait-until #(= (w/execute-script "return document.readyState") "complete")))

(defn odota-angular-pyyntoa []
  (odota-sivun-latautumista)
  (WaitForAngularRequestsToFinish/waitForAngularRequestsToFinish
    (:webdriver w/*driver*)))

(defn luo-webdriver! []
  (let [driver (init-driver (FirefoxDriver. (doto (DesiredCapabilities.)
                                              (.setCapability
                                                CapabilityType/UNEXPECTED_ALERT_BEHAVIOUR
                                                UnexpectedAlertBehaviour/IGNORE))))]
    (w/set-driver! driver)
    (-> driver :webdriver .manage .timeouts (.setScriptTimeout 30 TimeUnit/SECONDS))))

(defn puhdista-selain []
  (w/to "about:blank")
  (odota-sivun-latautumista))

(defn tarkista-js-virheet [f]
  (let [tulos (f)
        js-virheet (try
                     (w/execute-script "return window.jsErrors")
                     (catch UnhandledAlertException _
                       (-> w/*driver* :webdriver .switchTo .alert .accept)
                       (w/execute-script "return window.jsErrors")))]
    (is (empty? js-virheet))
    (w/execute-script "window.jsErrors = []")
    tulos))

(defn tarkista-otsikkotekstit [tulos]
  (let [tarkistettavat-elementit ["h1" "h2" "h3" "label" "th" ".table-header .table-cell"]
        tarkistukset (for [elementti tarkistettavat-elementit]
                       {:elementti elementti :tyhjia (count (w/find-elements {:css (str elementti ":empty:not(.ng-hide)")}))})]

    (is (every? #(= (:tyhjia %) 0) tarkistukset)))
  tulos)

(defn with-webdriver* [f]
  (if (bound? #'*ng*)
    (do
      (try
        (puhdista-selain)
        (catch UnhandledAlertException _
          (-> w/*driver* :webdriver .switchTo .alert .accept)))
      (-> (tarkista-js-virheet f)
          (tarkista-otsikkotekstit)))
    (try
      (luo-webdriver!)
      (binding [*ng* (ByAngular. (:webdriver w/*driver*))]
        (-> (tarkista-js-virheet f)
            (tarkista-otsikkotekstit)))
      (finally
        (w/quit)))))

(defmacro with-webdriver [& body]
  `(with-webdriver* (fn [] ~@body)))

(defn aitu-url [polku]
  (str (or (System/getenv "AITU_URL")
           "http://localhost:8080")
       polku))

(defn avaa [polku]
  (let [url (aitu-url polku)]
    (w/to url)
    (try
      (w/wait-until #(= (w/current-url) url))
      (catch TimeoutException e
        (println (str "Odotettiin selaimen siirtyvän URLiin '" url "'"
                      ", mutta sen URL oli " (w/current-url)))
        (throw e)))
    (odota-angular-pyyntoa)))

(defn avaa-uudelleenladaten [polku]
  (puhdista-selain)
  (avaa polku))

(defn sivun-otsikko []
  (w/text "#content h1"))

(defn aseta-inputtiin-arvo-jquery-selektorilla [selektori arvo]
  (w/execute-script (str selektori ".val('" arvo "').trigger('input')")))

(defn tyhjenna-input [ng-model-nimi]
  (aseta-inputtiin-arvo-jquery-selektorilla (str "$('input[ng-model=\"" ng-model-nimi "\"]')") ""))

(defn tyhjenna-datepicker-input [valittu-pvm-model]
  (aseta-inputtiin-arvo-jquery-selektorilla (str "$('fieldset[valittu-pvm=\"" valittu-pvm-model "\"] input[type=\"text\"]')") ""))

(defn elementin-teksti [binding-name]
  (w/text (w/find-element (-> *ng*
                            (.binding binding-name)))))

(defn enum-elementin-teksti [nimi]
  (w/text {:css (str "span[nimi=" nimi "]:not([class*='ng-hide'])")}))

(defn viestin-teksti [] (some-> (w/find-element {:css ".api-method-feedback p.message"})  w/text))

(defn viestit-virheellisista-kentista []
  (vec (map w/text (w/find-elements (-> *ng*
                                     (.repeater "virhe in palaute.virheet"))))))

(defn elementilla-luokka? [elementti luokka]
  (->
    elementti
    (w/attribute "class")
    (.contains luokka)))

(defn tallenna []
  (w/click "button[ng-click=\"tallenna()\"]")
  (odota-angular-pyyntoa))

(defn valitse-select2-optio
  "Valitsee ensimmäisen option hakuehto listalta"
  [malli tunnistekentta hakuehto ]
  (let [select2-container-selector (str "fieldset"
                                        "[model=\"" malli "\"]"
                                        "[model-id-property=\"" tunnistekentta "\"]"
                                        " div.select2-container")]
    (w/execute-script (str "$('" select2-container-selector "').data('select2').open()")))
  (w/wait-until #(-> (w/find-elements {:css "#select2-drop input.select2-input"}) (count) (> 0)))
  (w/clear "#select2-drop input")
  (w/input-text "#select2-drop input" hakuehto)
  (w/wait-until #(-> (w/find-elements {:css "#select2-drop input.select2-active"}) (count) (= 0)))
  (w/click "#select2-drop .select2-results li:first-child"))

(defn syota-kenttaan [ng-model-nimi arvo]
  (tyhjenna-input ng-model-nimi)
  (w/input-text (str "input[ng-model=\"" ng-model-nimi "\"]") arvo))

(defn syota-pvm [ng-model-nimi pvm]
  (let [selector (str "fieldset[valittu-pvm=\"" ng-model-nimi "\"] input[type=\"text\"]")]
    (w/clear selector)
    (w/input-text selector pvm)))

(defn dialogi-nakyvissa? [teksti-re]
  (try
    (let [alert (-> w/*driver* :webdriver .switchTo .alert)]
      (boolean (re-find teksti-re (.getText alert))))
    (catch NoAlertPresentException _
      false)))

(defn peruutan-dialogin []
  (-> w/*driver* :webdriver .switchTo .alert .dismiss)
  (odota-angular-pyyntoa))

(defn hyvaksy-dialogi []
  (-> w/*driver* :webdriver .switchTo .alert .accept)
  (odota-angular-pyyntoa))

(def hyvaksyn-dialogin hyvaksy-dialogi)

(defn lista [lista-nimi]
  (into []
        (for [elements (w/find-elements {:css (str lista-nimi " tbody tr")})]
          (clojure.string/split (w/text elements) #"\n"))))

(defn listarivi [lista-nimi rivi]
  (clojure.string/join " " ((lista lista-nimi) rivi)))

(defn valitse-radiobutton [ng-model-nimi arvo]
  (let [selector (str "input[ng-model=\"" ng-model-nimi "\"][value=\"" arvo "\"]")]
    (w/click selector)))

(defn kirjoita-tekstialueelle [ng-model-nimi arvo]
  (aseta-inputtiin-arvo-jquery-selektorilla (str "$('textarea[ng-model=\"" ng-model-nimi "\"]')") arvo))

(defn hae-teksti-jquery-selektorilla
  [selektori]
  (some->
    (w/execute-script (str "return " selektori ".get(0)"))
    (.getText)))

(defn klikkaa-linkkia [teksti]
  (w/click {:tag :a :text teksti})
  (odota-angular-pyyntoa))

(defn odota-dialogia [teksti-re]
  (w/wait-until #(dialogi-nakyvissa? teksti-re)))

(defn tallenna-ja-hyvaksy-dialogi []
  ;; Ei voida käyttää tallenna-funktiota, koska Angularin odottaminen vaatii
  ;; JavaScriptin suorittamista, mikä ei onnistu, kun dialogi on auki.
  (w/click "button[ng-click=\"tallenna()\"]")
  (odota-dialogia #"")
  (hyvaksy-dialogi))
