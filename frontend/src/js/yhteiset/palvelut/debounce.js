'use strict';

angular.module('yhteiset.palvelut.debounce', [])

  .factory('debounce', ['$timeout', function($timeout) {

    return function(fn, delay) {
      var promise,
          thisArg;

      return function(){
        var args = arguments;
        thisArg = this;
        $timeout.cancel(promise);
        promise = $timeout(function(){
          fn.apply(thisArg, args);
        }, delay);
      };
    };
  }]);
