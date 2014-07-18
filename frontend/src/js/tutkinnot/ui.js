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
                                'yhteiset.direktiivit.hakuvalitsin',
                                'yhteiset.direktiivit.palaaHakuun',
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

  .factory('TutkintoHakuModel', function() {
    return {
      tutkinnonNimi : '',
      opintoala : {},
      tutkinnot : null,
      nykyinenSivu : 1
    };
  })

  .controller('TutkinnotController', ['Tutkinto',
                                      'TutkintoHakuModel',
                                      '$scope',
                                      '$rootScope',
                                      '$filter',
                                      'debounce',
                                      'hakuAsetukset',
                                     function(Tutkinto,
                                              TutkintoHakuModel,
                                              $scope,
                                              $rootScope,
                                              $filter,
                                              debounce,
                                              asetukset) {

    $scope.hakuModel = TutkintoHakuModel;

    function tutkinnotHakuVastaus(tutkinnot) {
      TutkintoHakuModel.nykyinenSivu = 1;
      TutkintoHakuModel.tutkinnot = $filter('jarjestaLokalisoidullaNimella')(tutkinnot, 'nimi');
    }

    function hae() {
      var tutkinnonNimi = TutkintoHakuModel.tutkinnonNimi;
      var opintoala = _.isEmpty(TutkintoHakuModel.opintoala) ? null : TutkintoHakuModel.opintoala.opintoala_tkkoodi;
      if(tutkinnonNimi.length >= asetukset.minHakuehtoPituus || opintoala) {
        Tutkinto.haeEhdoilla({nimi: tutkinnonNimi, opintoala: opintoala}, tutkinnotHakuVastaus);
      }
    }

    $scope.hakuehdotMuuttuneet = debounce(hae, asetukset.viive);
  }])

  .controller('TutkintoController',
    ['Tutkinto', '$scope', '$routeParams', 'dateFilter',
    function(Tutkinto, $scope, $routeParams, dateFilter) {
      $scope.tutkinto = Tutkinto.hae($routeParams.tutkintotunnus);
      $scope.tutkinto.$promise.then(function(){
        $scope.siirtymaajan_loppupvm_muotoiltu =
          dateFilter($scope.tutkinto.siirtymaajan_loppupvm, 'd.M.yyyy');
      });
    }]);

