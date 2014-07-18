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

  beforeEach(module('tutkinnot.ui'));

  beforeEach(inject(function(_$timeout_, _$httpBackend_, $rootScope, $controller) {
    $timeout = _$timeout_;
    $httpBackend = _$httpBackend_;
    $scope = $rootScope.$new();
    $controller('TutkinnotController', {$scope: $scope});
  }));

  it('Liian lyhyen nimen syöttäminen ei tee hakua', function(){
    $scope.hakuModel.tutkinnonNimi = 'A';
    $scope.hakuehdotMuuttuneet();
    $timeout.flush();
    expect($scope.hakuModel.tutkinnot).toBeNull();
  });

  it('Riittävän pitkän nimen syöttäminen tekee haun', function(){
    $httpBackend.whenGET('').respond([{nimi_fi: "Autoalan perustutkinto"}]);
    $scope.hakuModel.tutkinnonNimi = 'Auto';
    $scope.hakuehdotMuuttuneet();
    $timeout.flush();
    $httpBackend.flush();
    expect($scope.hakuModel.tutkinnot.length).toBeGreaterThan(0);
  });

});
