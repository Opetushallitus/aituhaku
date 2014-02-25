'use strict';

angular.module('yhteiset.palvelut.i18n', ['ngResource'])

  .factory('kieli', ['$location', function($location) {
    var kieli = $location.search().kieli;
    return kieli ? kieli : 'fi';
  }])

  .factory('i18n', ['$resource', 'kieli', function($resource, kieli) {

    var i18nResource = $resource('api/i18n/:kieli');

    return i18nResource.get({kieli : kieli});

  }]);
