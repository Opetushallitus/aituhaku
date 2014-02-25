'use strict';

angular.module('aituhaku', ['tutkinnot.ui', 'toimikunnat.ui'])

  .constant('asetukset', {
    requestTimeout : 120000 //2min timeout kaikille pyynnöille
  });
