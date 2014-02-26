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

  .controller('TutkinnotController', ['Tutkinto', '$scope', function(Tutkinto, $scope) {
    $scope.hakuehto = {
      nimi: ''
    };

    $scope.hae = function(nimi) {
      Tutkinto.haeNimella(nimi, function(tutkinnot) { $scope.tutkinnot = tutkinnot; });
    };
  }])

  .controller('TutkintoController', ['Tutkinto', '$scope', '$routeParams', function(Tutkinto, $scope, $routeParams) {
    $scope.tutkinto = Tutkinto.hae($routeParams.tutkintotunnus);
  }]);

