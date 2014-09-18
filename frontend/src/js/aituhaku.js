// Copyright (c) 2013 The Finnish National Board of Education - Opetushallitus
//
// This program is free software:  Licensed under the EUPL, Version 1.1 or - as
// soon as they will be approved by the European Commission - subsequent versions
// of the EUPL (the "Licence");
//
// You may not use this work except in compliance with the Licence.
// You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// European Union Public Licence for more details.

'use strict';

angular.module('aituhaku', ['angular-loading-bar',
                            'tutkinnot.ui',
                            'toimikunnat.ui',
                            'jarjestajat.ui',
                            'yhteiset.direktiivit.copyright',
                            'yhteiset.palvelut.i18n',
                            'yhteiset.suodattimet.lokalisoi',
                            'yhteiset.suodattimet.sprintf',
                            'yhteiset.palvelut.virheLogitus',
                            'ui.select2'
                            ])

  .controller('AituhakuController', ['$scope', 'i18n', '$filter', function($scope, i18n, $filter){
    $scope.i18n = i18n;
    $scope.baseUrl = _.has(window, 'ophBaseUrl') ?  window.ophBaseUrl : '';
    $scope.timestamp = $filter('date')(new Date(), 'dd.MM.yyyy HH:mm');

    $scope.printPage = function(){
      window.print();
    };

  }])

  .constant('asetukset', {
    requestTimeout : 120000 //2min timeout kaikille pyynnöille
  })

  .config(['cfpLoadingBarProvider', function(cfpLoadingBarProvider) {
    cfpLoadingBarProvider.latencyThreshold = 100;
    cfpLoadingBarProvider.includeSpinner = false;
  }])

  .directive('kielenVaihto', ['kieli', function(kieli){
    return {
      restrict: 'E',
      templateUrl : 'template/kielen_vaihto.html',
      replace: true,
      link: function(scope) {
        scope.kieli = kieli;
      }
    };
  }])

  .factory('$exceptionHandler', ['virheLogitus', function(virheLogitus) {
    return virheLogitus;
  }]);
