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
  var Tutkinto;
  var $timeout;

  beforeEach(module('tutkinnot.ui'));

  beforeEach(module(function($provide){
    Tutkinto = jasmine.createSpyObj('Tutkinto', ['haeEhdoilla']);
    $provide.value('Tutkinto', Tutkinto);
  }));

  beforeEach(inject(function(TutkinnotControllerFunktiot, _$timeout_) {
    f = TutkinnotControllerFunktiot;
    $timeout = _$timeout_;
  }));

  describe('hakuehdot', function(){
    it('ei opintoalaa', function(){
      var hakuModel = {tutkinnonNimi: 'Auto'};
      expect(f.hakuehdot(hakuModel)).toEqual({nimi: 'Auto', opintoala: null});
    });

    it('Opintoala', function(){
      var hakuModel = {tutkinnonNimi: 'Auto',
                       opintoala: '123'};
      expect(f.hakuehdot(hakuModel)).toEqual({nimi: 'Auto', opintoala: '123'});
    });
  });

  describe('riittavatHakuehdot', function(){
    it('Pitkä tutkinnon nimi ilman opintoalaa', function(){
      expect(f.riittavatHakuehdot({nimi: 'Auto'})).toBe(true);
    });

    it('Lyhyt tutkinnon nimi ilman opintoalaa', function(){
      expect(f.riittavatHakuehdot({nimi: 'A'})).toBe(false);
    });

    it('Lyhyt tutkinnon nimi ja opintoala', function(){
      expect(f.riittavatHakuehdot({nimi: 'A', opintoala: '123'})).toBe(true);
    });
  });

  describe('paivitaHakutulokset', function(){
    it('vaihtaa esiin ensimmäisen hakutulossivun', function(){
      var hakuModel = {nykyinenSivu: 3};
      f.paivitaHakutulokset(hakuModel, []);
      expect(hakuModel.nykyinenSivu).toEqual(1);
    });

    it('järjestää hakutulokset tutkinnon nimen perusteella', function(){
      var hakuModel = {};
      f.paivitaHakutulokset(hakuModel, [{nimi_fi: 'b'}, {nimi_fi: 'a'}]);
      expect(hakuModel.tutkinnot).toEqual([{nimi_fi: 'a'}, {nimi_fi: 'b'}]);
    });
  });

  describe('opintoalatKoulutusaloista', function() {
    it('muodostaa koulutusalalistasta opintoala-mapin', function() {
      var koulutusalat = [{opintoalat: [{opintoala_tkkoodi: '1',
                                         opintoala_nimi_fi: '1 (fi)',
                                         opintoala_nimi_sv: '1 (sv)'},
                                        {opintoala_tkkoodi: '2',
                                         opintoala_nimi_fi: '2 (fi)',
                                         opintoala_nimi_sv: '2 (sv)'}]},
                          {opintoalat: [{opintoala_tkkoodi: '3',
                                         opintoala_nimi_fi: '3 (fi)',
                                         opintoala_nimi_sv: '3 (sv)'}]}];
      var opintoalat = {'1': {opintoala_tkkoodi: '1',
                              opintoala_nimi_fi: '1 (fi)',
                              opintoala_nimi_sv: '1 (sv)'},
                        '2': {opintoala_tkkoodi: '2',
                              opintoala_nimi_fi: '2 (fi)',
                              opintoala_nimi_sv: '2 (sv)'},
                        '3': {opintoala_tkkoodi: '3',
                              opintoala_nimi_fi: '3 (fi)',
                              opintoala_nimi_sv: '3 (sv)'}};
      expect(f.opintoalatKoulutusaloista(koulutusalat)).toEqual(opintoalat);
    });
  });

  describe('hae', function(){
    it('ei tee hakua puutteellisilla hakuehdoilla', function(){
      var callback = jasmine.createSpy('callback');
      f.hae({nimi: ''}, callback);
      $timeout.flush();
      expect(Tutkinto.haeEhdoilla.calls.any()).toEqual(false);
    });

    it('tekee haun, jos riittävät hakuehdot annettu', function(){
      var callback = jasmine.createSpy('callback');
      f.hae({nimi: 'Auto'}, callback);
      $timeout.flush();
      expect(Tutkinto.haeEhdoilla.calls.allArgs()).toEqual([[{nimi: 'Auto'}, callback]]);
    });

    it('kerää useat peräkkäiset hakupyynnöt yhteen hakuun', function(){
      var callback = jasmine.createSpy('callback');
      f.hae({nimi: 'Autoa'}, callback);
      f.hae({nimi: 'Autoal'}, callback);
      f.hae({nimi: 'Autoala'}, callback);
      $timeout.flush();
      expect(Tutkinto.haeEhdoilla.calls.allArgs()).toEqual([[{nimi: 'Autoala'}, callback]]);
    });
  });

});
