// Copyright (c) 2013 The Finnish National Board of Education - Opetushallitus
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

angular.module('jarjestajat.ui', ['jarjestajat.jarjestaja',
                                  'yhteiset.suodattimet.jarjestaLokalisoidullaNimella',
                                  'ngRoute',
                                  'yhteiset.direktiivit.palaaHakuun'])

  .config(['$routeProvider', function($routeProvider) {
    $routeProvider
      .when('/jarjestaja/:oppilaitoskoodi', {
        controller: 'JarjestajaController',
        templateUrl: 'template/jarjestajat/jarjestaja.html'
      });
  }])

  .controller('JarjestajaController', ['Jarjestaja', '$routeParams', '$scope', '$filter', function(Jarjestaja, $routeParams, $scope, $filter) {
    function jarjestaTutkinnot(hakutulos) {
      $scope.nykyinenSivu = 1;
      $scope.jarjestaja = hakutulos;
      $scope.jarjestaja.tutkinnot = $filter('jarjestaLokalisoidullaNimella')(hakutulos.tutkinnot, 'nimi');
    }

    Jarjestaja.get({oppilaitoskoodi: $routeParams.oppilaitoskoodi}, jarjestaTutkinnot);
  }]);

