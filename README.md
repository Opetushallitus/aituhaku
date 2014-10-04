Näyttötutkintohaku
==================

* Aituhaku käyttää Aitu-sovelluksen tietokantaa oman skeemansa näkymien kautta. Tietokannan alustusskriptit löytyvät [Aitun lähdekoodin](https://github.com/Opetushallitus/aitu) kautta.
* Aitun lähdekoodin mukana on myös virtuaalikoneet ja automatisoidut asennusskriptit, joilla tietokannan saa alustettua. 
* Aituhaun paikallista käyttöä ja kehitystä varten, katso frontend hakemistossa oleva readme ja noudata sen ohjeita.
* Näyttötutkintohaku on julkaistu ja tuotannossa. Näkyy osoitteessa [www.nayttotutkintohaku.fi](http://nayttotutkintohaku.fi).

# Kehitystyö

* Aituhaun kehitystyö: Katso dev/user.clj. Sen avulla voi REPL:ssä helposti hallita palvelimen käynnistelyä. 
 Ideana on ns. [Stuart Sierra workflow](http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded)

* Eclipse: Luo projekti puhtaana projektina, ei Clojure-projektina. Valitse sen jälkeen Convert to Leiningen project.
   (Counterclockwise luo muuten turhia default-tiedostoja, jotka joutuu käsin poistamaan.)
