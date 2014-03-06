'use strict';

angular.module('tutkinnot.tutkinto', ['ngResource'])
  .factory('Tutkinto', ['$resource', function($resource) {
    var resource = $resource(null, null, {
      hakuEhdoilla: {
        method: 'GET',
        isArray: true,
        url: 'api/tutkinto/haku'
      },
      haku: {
        method: 'GET',
        url: 'api/tutkinto/:tutkintotunnus'
      }
    });

    return {
      haeEhdoilla : function(ehdot, successCallback, errorCallback) {
        return resource.hakuEhdoilla(ehdot, successCallback, errorCallback);
      },
      hae : function(tutkintotunnus, successCallback, errorCallback) {
        return resource.haku({tutkintotunnus: tutkintotunnus}, successCallback, errorCallback);
      }
    };
  }]);

