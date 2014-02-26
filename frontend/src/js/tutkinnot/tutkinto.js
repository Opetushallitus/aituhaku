'use strict';

angular.module('tutkinnot.tutkinto', ['ngResource'])
  .factory('Tutkinto', ['$resource', function($resource) {
    var Resource = $resource('api/tutkinto/haku', null, {
      hakuNimella: {
        method: 'GET',
        isArray: true,
        id: 'tutkintohaku'
      },
      haku: {
        method: 'GET',
        url: 'api/tutkinto/:tutkintotunnus'
      }
    });

    Resource.haeNimella = function(termi, successCallback, errorCallback) {
      return Resource.hakuNimella({termi: termi}, successCallback, errorCallback);
    };
    Resource.hae = function(tutkintotunnus, successCallback, errorCallback) {
      return Resource.haku({tutkintotunnus: tutkintotunnus}, successCallback, errorCallback);
    };

    return Resource;
  }]);

