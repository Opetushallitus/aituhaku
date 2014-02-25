'use strict';

angular.module('toimikunnat.toimikunta', ['ngResource'])
  .factory('Toimikunta', ['$resource', function($resource) {
    return $resource('api/toimikunta/:tkunta', {tkunta: '@tkunta'}, {
      get: {
        method: 'GET',
        id: 'toimikunta'
      }
    });
  }]);

