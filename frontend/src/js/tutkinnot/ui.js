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
                                'yhteiset.direktiivit.hakuvalitsin',
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
    $scope.tutkinnonNimi = '';
    $scope.opintoala = {};

    function tutkinnotHakuVastaus(tutkinnot) {
      $scope.hakujaKaynnissa--;
      $scope.tutkinnot = $filter('jarjestaLokalisoidullaNimella')(tutkinnot, 'nimi');
    }

    function hae() {
      var tutkinnonNimi = $scope.tutkinnonNimi;
      var opintoala = _.isEmpty($scope.opintoala) ? null : $scope.opintoala.opintoala_tkkoodi;
      if(tutkinnonNimi.length >= asetukset.minHakuehtoPituus || opintoala) {
        $scope.hakujaKaynnissa++;
        Tutkinto.haeEhdoilla({nimi: tutkinnonNimi, opintoala: opintoala}, tutkinnotHakuVastaus);
      }
    }

    var hakuehdotMuuttuneet = debounce(hae, asetukset.viive);

    $scope.$watch('tutkinnonNimi', hakuehdotMuuttuneet);
    $scope.$watch('opintoala.opintoala_tkkoodi', hakuehdotMuuttuneet);

  }])

  .controller('TutkintoController', ['Tutkinto', '$scope', '$routeParams', function(Tutkinto, $scope, $routeParams) {
    $scope.tutkinto = Tutkinto.hae($routeParams.tutkintotunnus);
  }]);

