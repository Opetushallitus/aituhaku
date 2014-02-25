'use strict';

angular.module('yhteiset.direktiivit.hakutulokset', [
    'yhteiset.direktiivit.hakutuloslaskuri',
    'yhteiset.direktiivit.sivutusnavigaatio',
    'yhteiset.suodattimet.sivuta'
  ])
  .directive('hakutulokset', function() {
    return {
      restrict: 'E',
      replace: true,
      scope: {
        hakutulokset: '=',
        tuloksiaSivulla: '=',
        otsikot: '='
      },
      transclude: true,
      templateUrl: 'template/yhteiset/direktiivit/hakutulokset.html',
      link: function(scope) {
        scope.nykyinenSivu = 1;
        scope.$watchCollection('hakutulokset', function() {
          scope.nykyinenSivu = 1;
        });
      }
    };
  });
