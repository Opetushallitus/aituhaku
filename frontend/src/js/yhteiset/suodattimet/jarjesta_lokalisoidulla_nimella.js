'use strict';

angular.module('yhteiset.suodattimet.jarjestaLokalisoidullaNimella', [])
  .filter('jarjestaLokalisoidullaNimella', ['$filter','kieli', function($filter, kieli) {
    return function(entityt, kentta, reverse){
      return $filter('orderBy')(entityt, kentta + '_' + kieli, reverse);
    };
  }]);
