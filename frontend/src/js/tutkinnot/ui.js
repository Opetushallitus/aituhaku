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

angular.module('tutkinnot.ui', ['tutkinnot.tutkinto',
                                'yhteiset.direktiivit.hakutulokset',
                                'yhteiset.suodattimet.jarjestaLokalisoidullaNimella',
                                'yhteiset.palvelut.debounce',
                                'yhteiset.direktiivit.latausindikaattori',
                                'ngRoute'])

  .config(['$routeProvider', function($routeProvider) {
    $routeProvider
      .when('/tutkinnot', {
        controller: 'TutkinnotController',
        templateUrl: 'template/tutkinnot/tutkinnot.html'
      })
      .when('/tutkinto/:tutkintotunnus', {
        controller: 'TutkintoController',
        templateUrl: 'template/tutkinnot/tutkinto.html'
      })
      .otherwise({
        redirectTo: '/tutkinnot'
      });
  }])

  .constant('hakuAsetukset', {
    viive : 500,
    minHakuehtoPituus : 3
  })

  .controller('TutkinnotController', ['Tutkinto', '$scope', '$filter', 'debounce', 'hakuAsetukset', function(Tutkinto, $scope, $filter, debounce, asetukset) {

    $scope.hakujaKaynnissa = 0;

    function tutkinnotHakuVastaus(tutkinnot) {
      $scope.hakujaKaynnissa--;
      $scope.tutkinnot = $filter('jarjestaLokalisoidullaNimella')(tutkinnot, 'nimi');
    }

    function hae() {
      var hakuehto = $scope.hakuehto;

      if(hakuehto.nimi.length >= asetukset.minHakuehtoPituus || hakuehto.opintoala.length >= asetukset.minHakuehtoPituus) {
        $scope.hakujaKaynnissa++;
        Tutkinto.haeEhdoilla(hakuehto, tutkinnotHakuVastaus);
      }
    }

    $scope.hakuehto = {
      nimi: '',
      opintoala: ''
    };

    $scope.hakuehdotMuuttuneet = debounce(hae, asetukset.viive);

  }])

  .controller('TutkintoController', ['Tutkinto', '$scope', '$routeParams', function(Tutkinto, $scope, $routeParams) {
    $scope.tutkinto = Tutkinto.hae($routeParams.tutkintotunnus);
  }]);

