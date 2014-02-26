'use strict';

angular.module('toimikunnat.ui', ['toimikunnat.toimikunta', 'ngRoute'])

  .config(['$routeProvider', function($routeProvider) {
    $routeProvider
      .when('/toimikunta/:tkunta', {
        controller: 'ToimikuntaController',
        templateUrl: 'template/toimikunnat/toimikunta.html'
      });
  }])

  .controller('ToimikuntaController', ['Toimikunta', '$routeParams', '$scope', function(Toimikunta, $routeParams, $scope) {
    $scope.toimikunta = Toimikunta.get({tkunta: $routeParams.tkunta});
  }]);

