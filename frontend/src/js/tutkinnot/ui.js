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
                                'tutkinnot.opintoala',
                                'tutkinnot.kielet',
                                'yhteiset.direktiivit.hakutulokset',
                                'yhteiset.suodattimet.jarjestaLokalisoidullaNimella',
                                'yhteiset.palvelut.debounce',
                                'yhteiset.direktiivit.palaaHakuun',
                                'yhteiset.palvelut.i18n',
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

  // Modelia ei voida alustaa TutkinnotControllerissa, koska silloin hakuehdot
  // tyhjenisivät aina kun hakusivulle navigoidaan (hakuehtojen halutaan
  // säilyvän esim. kun hakusivulle palataan selaimen Takaisin-napilla).
  .factory('TutkintoHakuModel', ['kieli',  function(kieli) {
    return {
      tutkinnonNimi : '',
      nimikieli: kieli, // i18n.kieli, // i18n.kieli 
      opintoala : '',
      opintoalanNimi : {},
      tutkinnot : null,
      nykyinenSivu : 1
    };
  }])

  .factory('TutkinnotControllerFunktiot', ['hakuAsetukset',
                                           '$filter',
                                           'Tutkinto',
                                           'debounce',
                                           function(asetukset, $filter, Tutkinto, debounce){
    function hakuehdot(hakuModel) {
      return {nimi: hakuModel.tutkinnonNimi,
    	      nimikieli: hakuModel.nimikieli, 
              opintoala: _.isEmpty(hakuModel.opintoala) ? null : hakuModel.opintoala,
              kieli: _.isEmpty(hakuModel.kieli) ? null : hakuModel.kieli};
    }

    function riittavatHakuehdot(hakuehdot) {
      return hakuehdot.nimi.length >= asetukset.minHakuehtoPituus || !!hakuehdot.opintoala || !!hakuehdot.kieli;
    }

    function paivitaHakutulokset(hakuModel, tutkinnot) {
      hakuModel.nykyinenSivu = 1;
      hakuModel.tutkinnot = $filter('jarjestaLokalisoidullaNimella')(tutkinnot, 'nimi');
    }

    function opintoalatKoulutusaloista(koulutusalat) {
      return _(koulutusalat).map('opintoalat')
                            .flatten()
                            .indexBy('opintoala_tkkoodi')
                            .value();
    }

    var hae = debounce(function(hakuehdot, callback){
      if (riittavatHakuehdot(hakuehdot)) {
        Tutkinto.haeEhdoilla(hakuehdot, callback);
      }
    }, asetukset.viive);

    return {
      hakuehdot: hakuehdot,
      riittavatHakuehdot: riittavatHakuehdot,
      paivitaHakutulokset: paivitaHakutulokset,
      opintoalatKoulutusaloista: opintoalatKoulutusaloista,
      hae: hae
    };
  }])

  .controller('TutkinnotController', ['TutkinnotControllerFunktiot',
                                      'Tutkinto',
                                      'TutkintoHakuModel',
                                      'Opintoala',
                                      'Kielet',
                                      'kieli',
                                      '$scope',
                                     function(f,
                                              Tutkinto,
                                              TutkintoHakuModel,
                                              Opintoala,
                                              Kielet,
                                              kieli,
                                              $scope) {
    var opintoalat = {};
    $scope.hakuModel = TutkintoHakuModel;
    $scope.select2Options = {
      allowClear: true
    };
    $scope.opintoalaOrder = (kieli === 'fi' ? 'opintoala_nimi_fi' : 'opintoala_nimi_sv');
    Opintoala.haku(function(data) {
      $scope.koulutusalat = data;
      opintoalat = f.opintoalatKoulutusaloista(data);
    });
    Kielet.haku(function(data) {
      $scope.kielet = data;
    });

    var haunLaukaisevatKentat = ['tutkinnonNimi', 'opintoala', 'kieli'];

    _.each(haunLaukaisevatKentat, function(k){
      $scope.$watch('hakuModel.'+k, function(n, o){
        // Jätetään Angularin alustuksen aiheuttama kutsu huomiotta, ks.
        // http://stackoverflow.com/a/18915585
        if (n !== o) {
          f.hae(f.hakuehdot($scope.hakuModel), function(tutkinnot){
            f.paivitaHakutulokset($scope.hakuModel, tutkinnot);
          });
        }
        if(k === 'opintoala') {
          $scope.hakuModel.opintoalanNimi = opintoalat[n] || {};
        }
      });
    });
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
