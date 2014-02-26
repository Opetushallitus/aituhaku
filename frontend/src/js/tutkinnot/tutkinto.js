'use strict';

angular.module('tutkinnot.tutkinto', ['ngResource'])
  .factory('Tutkinto', ['$resource', function($resource) {
    var Resource = $resource('api/tutkinto/haku', null, {
      haku: {
        method: 'GET',
        isArray: true,
        id: 'tutkintohaku'
      },
      hakuTunnuksella: {
        method: 'GET',
        url: 'api/tutkinto/:tutkintotunnus'
      }
    });

    Resource.hae = function(termi, successCallback, errorCallback) {
      return Resource.haku({termi: termi}, successCallback, errorCallback);
    };
    Resource.tiedot = function(tutkintotunnus, successCallback, errorCallback) {
      return Resource.hakuTunnuksella({tutkintotunnus: tutkintotunnus}, successCallback, errorCallback);
    };

    return Resource;
  }]);

