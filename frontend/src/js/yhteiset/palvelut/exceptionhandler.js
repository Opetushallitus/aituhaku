// Copyright (c) 2013 The Finnish National Board of Education - Opetushallitus
//
// This program is free software:  Licensed under the EUPL, Version 1.1 or - as
// soon as they will be approved by the European Commission - subsequent versions
// of the EUPL (the "Licence");
//
// You may not use this work except in compliance with the Licence.
// You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// European Union Public Licence for more details.

'use strict';

/*global $: true, hakuBaseUrl: true, printStackTrace: true */

angular.module('yhteiset.palvelut.virheLogitus', [])

    .factory('virheLogitusApi', ['$log', '$window', function($log, $window ){
    return {
      lahetaPalvelimelle : function(poikkeus, aiheuttaja){

        try {
          var virheviesti = poikkeus.toString();
          var stackTrace = printStackTrace({ e: poikkeus });

          $.ajax({
            type: 'POST',
            url:  hakuBaseUrl + '/api/jslog/virhe',
            contentType: 'application/json',
            data: angular.toJson({
              virheenUrl: $window.location.href,
              userAgent : navigator.userAgent,
              virheviesti: virheviesti,
              stackTrace: stackTrace,
              cause: (aiheuttaja || '')
            })
          });
        } catch ( virhe ) {
          $log.log( virhe );
        }
      }
    };
  }])

  .factory('virheLogitus', ['$log', '$window', 'virheLogitusApi', function($log, $window, virheLogitusApi) {
    $window.jsErrors = []; //e2e testit keräävät virheet tästä

    function log( poikkeus, aiheuttaja ) {
      var jsErrorsMessage = poikkeus.message + ' (aiheuttanut "' + aiheuttaja + '")';

      $window.jsErrors.push(jsErrorsMessage);

      $log.error.apply( $log, arguments );
      virheLogitusApi.lahetaPalvelimelle(poikkeus, aiheuttaja);
    }

    return log;
  }]);