'use strict';

angular.module('aituhaku', ['tutkinnot.ui'])

  .constant('asetukset', {
    requestTimeout : 120000 //2min timeout kaikille pyynnöille
  });
