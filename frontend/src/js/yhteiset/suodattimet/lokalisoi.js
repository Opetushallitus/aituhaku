'use strict';

angular.module('yhteiset.suodattimet.lokalisoi', [])
  .filter('lokalisoi', ['kieli', function(kieli){
    return function(_, obj, prop) {
      return obj ? obj[prop + '_' + kieli] : '';
    };
  }]);
