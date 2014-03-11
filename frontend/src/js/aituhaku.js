'use strict';

angular.module('aituhaku', ['tutkinnot.ui', 'toimikunnat.ui', 'yhteiset.palvelut.i18n', 'yhteiset.suodattimet.lokalisoi'])

  .controller('AituhakuController', ['$scope', 'i18n', function($scope, i18n){
    $scope.i18n = i18n;
    $scope.baseUrl = _.has(window, 'hakuBaseUrl') ?  window.hakuBaseUrl : '';
  }])

  .constant('asetukset', {
    requestTimeout : 120000 //2min timeout kaikille pyynnöille
  })

  .directive('kielenVaihto', ['kieli', function(kieli){
    return {
      restrict: 'E',
      templateUrl : 'template/kielen_vaihto.html',
      replace: true,
      link: function(scope) {
        scope.kieli = kieli;
      }
    };
  }]);
