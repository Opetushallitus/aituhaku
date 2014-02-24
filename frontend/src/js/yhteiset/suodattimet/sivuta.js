'use strict';

angular.module('yhteiset.suodattimet.sivuta', [])
  .filter('sivuta', function() {
    return function(suodatettava, nykyinenSivu, tuloksiaSivulla) {
      return _(suodatettava).rest((nykyinenSivu - 1)*tuloksiaSivulla).first(tuloksiaSivulla).value();
    };
  });
