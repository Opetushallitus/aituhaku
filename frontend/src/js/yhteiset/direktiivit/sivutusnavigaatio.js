'use strict';

angular.module('yhteiset.direktiivit.sivutusnavigaatio', ['ui.bootstrap', 'yhteiset.palvelut.i18n'])
  .directive('sivutusnavigaatio', ['i18n', function(i18n) {
    return {
      restrict: 'E',
      replace: true,
      templateUrl: 'template/yhteiset/direktiivit/sivutusnavigaatio.html',
      link : function(scope) {
        scope.i18n = i18n;
      }
    };
  }]);
