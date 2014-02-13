Frontend asennus:

-Asenna node ja npm
http://nodejs.org/download/

-Asenna grunt
> npm install -g grunt-cli

-Hakemistossa /frontend tee seuraavat:

> npm install
> grunt bower
> grunt sass-compile

Uuden bower kirjaston käyttöönotto:

> bower install <kirjaston-nimi> --save
- Tämä lataa kirjaston kokonaisuudessaan bower_components hakemistoon ja tallettaa dependencyn bower.json -tiedostoon.
- Katso bower_components -hakemistosta polku kirjaston .js -tiedostoon ja lisää polku Gruntfile.js:n bowercopy -conffin.

> grunt bower

SASS -tiedostojen kääntäminen

> grunt sass-compile

Tai jatkuva .scss muutosten seuranta ja kääntäminen:

> grunt sass-watch










