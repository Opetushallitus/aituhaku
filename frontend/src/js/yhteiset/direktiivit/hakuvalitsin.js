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

angular.module('yhteiset.direktiivit.hakuvalitsin', [])

.directive('hakuvalitsin', ['i18n', 'kieli',  function(i18n, kieli){
  return {
    restrict: 'E',
    replace: true,
    scope : {
      otsikko : '@',
      url : '@',
      model : '=',
      // Select2 hävittää saamastaan model-oliosta kenttiä valinnan
      // vaihtuessa. Tämän vuoksi ei voida antaa Angular-scopessa olevaa
      // modelia suoraan Select2:lle, vaan annetaan sille eri olio, ja
      // pidetään watcheilla niiden id- ja text-kentät synkassa.
      modelIdProperty : '@',
      modelTextProperty : '@',
      searchPropertyMap : '@'
    },
    templateUrl : 'template/yhteiset/direktiivit/hakuvalitsin.html',
    controller : function($scope) {
      var modelIdProp = $scope.modelIdProperty;
      var modelTextProp = $scope.modelTextProperty;
      var searchPropertyMap = $scope.$eval($scope.searchPropertyMap);

      $scope.selection = $scope.model || {};

      $scope.$watch('selection', function(value){
        if(value && value[modelIdProp]) {
          $scope.model = $scope.model || {};
          $scope.model[modelIdProp] = value[modelIdProp];
          $scope.model[modelTextProp] = value[modelTextProp];
        }
        else if ($scope.model) {
          delete $scope.model[modelIdProp];
          delete $scope.model[modelTextProp];
        }
      });

      $scope.$watch('model', function(value) {
        if (value) {
          $scope.selection[modelIdProp] = value[modelIdProp];
          $scope.selection[modelTextProp] = value[modelTextProp];
        }
      });

      function lokalisoituTeksti(obj, textProp) {
        var teksti = '';

        if(_.has(obj, textProp)) {
          teksti = obj[textProp];
        } else {
          teksti = obj[textProp + '_' + kieli];
        }
        return teksti;
      }

      function mapSearchResult(obj) {
        if (searchPropertyMap) {
          return _.transform(searchPropertyMap, function (result, toKey, fromKey) {
            result[toKey] = obj[fromKey];
          });
        } else {
          return obj;
        }
      }

      $scope.options = {
        width: '100%',
        minimumInputLength : 1,
        allowClear : true,
        ajax: {
          url : $scope.url,
          dataType: 'json',
          quietMillis: 500,
          data: function (term) {
            return {
              termi: term // search term
            };
          },
          results: function (data) {
            return {results: _.map(data, mapSearchResult)};
          }
        },
        formatResult : function(object) {
          return lokalisoituTeksti(object, modelTextProp);
        },
        formatSelection : function(object) {
          return lokalisoituTeksti(object, modelTextProp);
        },
        id : function(object) {
          return object[modelIdProp];
        },
        formatNoMatches: function () {
          return i18n.yleiset['ei-tuloksia'];
        },
        formatInputTooShort: function () {
          return i18n.yleiset['anna-hakuehto'];
        },
        formatSearching: function () {
          return i18n.yleiset.etsitaan;
        }
      };
    }
  };
}]);