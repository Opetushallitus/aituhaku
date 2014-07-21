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

describe('tutkinnot.ui.TutkinnotController', function(){

  var $timeout;
  var $httpBackend;
  var $scope;
  var $controller;

  beforeEach(module('tutkinnot.ui'));

  beforeEach(inject(function(_$timeout_, _$httpBackend_, $rootScope, _$controller_) {
    $timeout = _$timeout_;
    $httpBackend = _$httpBackend_;
    $controller = _$controller_;
    $scope = $rootScope.$new();
    $controller('TutkinnotController', {$scope: $scope});
  }));

  function syotaHakuehdot(ehdot) {
    _.assign($scope.hakuModel, ehdot);
    $scope.hakuehdotMuuttuneet();
    try{ $timeout.flush(); }catch(_){}
    try{ $httpBackend.flush(); }catch(_){}
  }

  it('Liian lyhyen nimen syöttäminen ei tee hakua', function(){
    syotaHakuehdot({tutkinnonNimi: 'A'});
    expect($scope.hakuModel.tutkinnot).toBeNull();
  });

  it('Riittävän pitkän nimen syöttäminen tekee haun', function(){
    $httpBackend.whenGET('').respond([{nimi_fi: 'Autoalan perustutkinto'}]);
    syotaHakuehdot({tutkinnonNimi: 'Auto'});
    expect($scope.hakuModel.tutkinnot.length).toBeGreaterThan(0);
  });

  it('Haun tila pysyy tallessa kun navigoidaan sivulta pois ja takaisin', function(){
    syotaHakuehdot({tutkinnonNimi: 'Auto'});
    $controller('TutkinnotController', {$scope: $scope});
    expect($scope.hakuModel.tutkinnonNimi).toEqual('Auto');
  });

});
