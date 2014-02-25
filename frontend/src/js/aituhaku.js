'use strict';

angular.module('aituhaku', ['tutkinnot.ui', 'toimikunnat.ui', 'yhteiset.palvelut.i18n', 'yhteiset.suodattimet.lokalisoi'])

  .controller('AituhakuController', ['$scope', 'i18n', function($scope, i18n){
    $scope.i18n = i18n;
  }])

  .constant('asetukset', {
    requestTimeout : 120000 //2min timeout kaikille pyynnöille
  });
