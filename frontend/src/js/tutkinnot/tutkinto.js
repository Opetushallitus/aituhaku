'use strict';

angular.module('tutkinnot.tutkinto', ['ngResource'])
  .factory('Tutkinto', ['$resource', function($resource) {
    var Resource = $resource('api/tutkinto/haku', null, {
      haku: {
        method: 'GET',
        isArray: true,
        id: 'tutkintohaku'
      }
    });

    Resource.hae = function(termi, successCallback, errorCallback) {
      return Resource.haku({termi: termi}, successCallback, errorCallback);
    };

    return Resource;
  }]);

