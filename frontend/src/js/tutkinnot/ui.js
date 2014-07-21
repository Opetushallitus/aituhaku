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

  .factory('TutkinnotControllerFunktiot', ['hakuAsetukset',
                                           '$filter',
                                           function(asetukset, $filter){
    function hakuehdot(hakuModel) {
      var tutkinnonNimi = hakuModel.tutkinnonNimi;
      var opintoala = _.isEmpty(hakuModel.opintoala) ? null : hakuModel.opintoala.opintoala_tkkoodi;
      if (tutkinnonNimi.length >= asetukset.minHakuehtoPituus || opintoala) {
        return {nimi: tutkinnonNimi, opintoala: opintoala};
      } else {
        return null;
      }
    }

    function paivitaHakutulokset(hakuModel, tutkinnot) {
      hakuModel.nykyinenSivu = 1;
      hakuModel.tutkinnot = $filter('jarjestaLokalisoidullaNimella')(tutkinnot, 'nimi');
    }

    return {
      hakuehdot: hakuehdot,
      paivitaHakutulokset: paivitaHakutulokset
    };
  }])

  .controller('TutkinnotController', ['TutkinnotControllerFunktiot',
                                      'Tutkinto',
                                      'TutkintoHakuModel',
                                      '$scope',
                                      '$rootScope',
                                      '$filter',
                                      'debounce',
                                      'hakuAsetukset',
                                     function(f,
                                              Tutkinto,
                                              TutkintoHakuModel,
                                              $scope,
                                              $rootScope,
                                              $filter,
                                              debounce,
                                              asetukset) {
    $scope.hakuModel = TutkintoHakuModel;

    function hae() {
      var hakuehdot = f.hakuehdot(TutkintoHakuModel);
      if (hakuehdot) {
        Tutkinto.haeEhdoilla(hakuehdot, function(tutkinnot){
          f.paivitaHakutulokset(TutkintoHakuModel, tutkinnot)
        });
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
