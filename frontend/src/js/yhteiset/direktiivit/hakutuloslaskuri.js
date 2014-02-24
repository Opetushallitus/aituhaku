'use strict';

angular.module('yhteiset.direktiivit.hakutuloslaskuri', [])
  .directive('hakutuloslaskuri', function() {
    return {
      restrict: 'E',
      replace: true,
      templateUrl: 'template/yhteiset/direktiivit/hakutuloslaskuri.html',
      link: function(scope) {
        scope.mista = function(nykyinenSivu, tuloksiaSivulla ) {
          return (nykyinenSivu - 1) * tuloksiaSivulla + 1;
        };
        scope.mihin = function(nykyinenSivu, tuloksiaSivulla, tuloksiaYhteensa) {
          return Math.min((nykyinenSivu - 1) * tuloksiaSivulla + tuloksiaSivulla, tuloksiaYhteensa);
        };
      }
    };
  });
