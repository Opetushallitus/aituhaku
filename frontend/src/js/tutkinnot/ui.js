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

