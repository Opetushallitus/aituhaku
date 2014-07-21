// Copyright (c) 2014 The Finnish National Board of Education - Opetushallitus
//
// This program is free software:  Licensed under the EUPL, Version 1.1 or - as
// soon as they will be approved by the European Commission - subsequent versions
// of the EUPL (the "Licence");
//
// You may not use this work except in compliance with the Licence.
// You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// European Union Public Licence for more details.

'use strict';

describe('tutkinnot.ui.TutkinnotControllerFunktiot', function(){

  var f;

  beforeEach(module('tutkinnot.ui'));

  beforeEach(inject(function(TutkinnotControllerFunktiot) {
    f = TutkinnotControllerFunktiot;
  }));

  describe('hakuehdot', function(){
    it('Pitkä tutkinnon nimi ilman opintoalaa', function(){
      expect(f.hakuehdot({tutkinnonNimi: 'Auto'}))
      .toEqual({nimi: 'Auto',
                opintoala: null});
    });

    it('Lyhyt tutkinnon nimi ilman opintoalaa', function(){
      expect(f.hakuehdot({tutkinnonNimi: 'A'}))
      .toBe(null);
    });

    it('Lyhyt tutkinnon nimi ja opintoala', function(){
      expect(f.hakuehdot({tutkinnonNimi: 'A',
                          opintoala: {opintoala_tkkoodi: '123'}}))
      .toEqual({nimi: 'A',
                opintoala: '123'});
    });
  });

  describe('paivitaHakutulokset', function(){
    it('järjestää hakutulokset tutkinnon nimen perusteella', function(){
      var hakuModel = {};
      f.paivitaHakutulokset(hakuModel, [{nimi_fi: "b"}, {nimi_fi: "a"}]);
      expect(hakuModel.tutkinnot).toEqual([{nimi_fi: "a"}, {nimi_fi: "b"}]);
    });
  })

});