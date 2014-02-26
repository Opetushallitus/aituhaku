'use strict';

angular.module('tutkinnot.ui', ['tutkinnot.tutkinto', 'yhteiset.direktiivit.hakutulokset', 'ngRoute'])

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

  .controller('TutkinnotController', ['Tutkinto', '$scope', 'hakuAsetukset', function(Tutkinto, $scope, asetukset) {

    function hae(nimi) {
      if(nimi && nimi.length >= asetukset.minHakuehtoPituus) {
        Tutkinto.haeNimella(nimi, function(tutkinnot) { $scope.tutkinnot = tutkinnot; });
      }
    }

    $scope.hakuehto = {
      nimi: ''
    };

    $scope.hae = _.debounce(hae, asetukset.viive);

  }])

  .controller('TutkintoController', ['Tutkinto', '$scope', '$routeParams', function(Tutkinto, $scope, $routeParams) {
    $scope.tutkinto = Tutkinto.hae($routeParams.tutkintotunnus);
  }]);

