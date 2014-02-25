'use strict';

angular.module('tutkinnot.ui', ['tutkinnot.tutkinto', 'yhteiset.direktiivit.hakutulokset', 'ngRoute'])

  .config(['$routeProvider', function($routeProvider) {
    $routeProvider
      .when('/tutkinnot', {
        controller: 'TutkinnotController',
        templateUrl: 'template/tutkinnot/tutkinnot.html'
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
      Tutkinto.hae(nimi, function(tutkinnot) { $scope.tutkinnot = tutkinnot; });
    };
  }]);

