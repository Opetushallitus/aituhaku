Näyttötutkintohaku
==================

[Näyttötutkintohaku.fi](http://www.nayttotutkintohaku.fi/) palvelun yleiskuvaus, arkkitehtuuridokumentaatio ja muu julkinen dokumentaatio löytyy CSC:n palvelusta: [Näyttötutkintohaku.fi kuvaus](https://confluence.csc.fi/pages/viewpage.action?pageId=58826670).

aituhaku on työnimi järjestelmälle.

# Lähdekoodin kuvaus

* Aituhaku käyttää Aitu-sovelluksen tietokantaa oman skeemansa näkymien kautta. Tietokannan alustusskriptit löytyvät [Aitun lähdekoodin](https://github.com/Opetushallitus/aitu) kautta.
* Aitun lähdekoodin mukana on myös virtuaalikoneet ja automatisoidut asennusskriptit, joilla tietokannan saa alustettua.
* Aituhaun paikallista käyttöä ja kehitystä varten, katso frontend hakemistossa oleva readme ja noudata sen ohjeita.

# Kehitystyö

* Aituhaun kehitystyö: Katso dev/user.clj. Sen avulla voi REPL:ssä helposti hallita palvelimen käynnistelyä.
 Ideana on ns. [Stuart Sierra workflow](http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded)

* Jos kehität Windows-koneella, aja ennen ensimmäistä serverin käynnistystä (user\uudelleenkaynnista!) komento "npm install --global windows-build-tools". Lisätietoa https://www.npmjs.com/package/windows-build-tools ja https://github.com/nodejs/node-gyp.

* Eclipse: Luo projekti puhtaana projektina, ei Clojure-projektina. Valitse sen jälkeen Convert to Leiningen project.
   (Counterclockwise luo muuten turhia default-tiedostoja, jotka joutuu käsin poistamaan.)

Toteutuskoodilla on riippuvuus yleiskäyttöisiä kirjastofunktioita sisältävään [clojure-utils](https://github.com/Opetushallitus/clojure-utils) repositoryyn. Tämä on järjestetty git submodulena.

# Testien ajaminen

Näyttötutkintohaku käyttää Aitun tietokantaa, joten myös testit edellyttävät tätä. Lisäksi end-to-end selaintestit hakemistossa *e2e* luovat testidataa Aitun rajapintojen avulla, joten näiden testien ajaminen edellyttää sitä että Aitu-sovellus on käynnistetty paikallisesti.



