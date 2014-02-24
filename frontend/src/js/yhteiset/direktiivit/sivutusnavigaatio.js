'use strict';

angular.module('yhteiset.direktiivit.sivutusnavigaatio', ['ui.bootstrap'])
  .directive('sivutusnavigaatio', function() {
    return {
      restrict: 'E',
      replace: true,
      templateUrl: 'template/yhteiset/direktiivit/sivutusnavigaatio.html'
    };
  });
