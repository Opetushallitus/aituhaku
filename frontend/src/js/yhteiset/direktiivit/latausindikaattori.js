'use strict';

angular.module('yhteiset.direktiivit.latausindikaattori', [])
  .directive('latausindikaattori', function() {
    return {
      restrict: 'A',
      templateUrl: 'template/yhteiset/direktiivit/latausindikaattori.html',
      scope : {
        lataa : '='
      },
      transclude : true
    };
  });
