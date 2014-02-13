Frontend asennus:

> npm install
> grunt bower
> grunt sass-compile

Uuden bower kirjaston käyttöönotto:

> bower install <kirjaston-nimi> --save
- Tämä lataa kirjaston kokonaisuudessaan bower_components hakemistoon ja tallettaa dependencyn bower.json -tiedostoon.
- Katso bower_components -hakemistosta polku kirjaston .js -tiedostoon ja lisää polku Gruntfile.js:n bowercopy -conffin.

> grunt bower

SASS tyylimuutokset

> grunt sass-compile

Tai jatkuva .scss muutosten seuranta:

> grunt sass-watch










