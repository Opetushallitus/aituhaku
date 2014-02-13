Frontend asennus:

> npm install
> grunt bower

Uuden bower kirjaston käyttöönotto:

> bower install <kirjaston-nimi> --save
- Tämä lataa kirjaston kokonaisuudessaan bower_components hakemistoon ja tallettaa dependencyn bower.json -tiedostoon.
- Katso bower_components -hakemistosta polku kirjaston .js -tiedostoon ja lisää polku Gruntfile.js:n bowercopy -conffin.

> grunt bower








