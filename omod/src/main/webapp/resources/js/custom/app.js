var relationshipMappingModule = angular.module('relationshipMappingModule', ['ui.bootstrap', 'ngRoute', 'ngSanitize',  'pascalprecht.translate']);

relationshipMappingModule.
    config(['$routeProvider', '$compileProvider', '$translateProvider', function ($routeProvider, $compileProvider, $translateProvider) {
        $compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|ftp|mailto|file):/);
        $routeProvider.
            when('/mappingHistory', {controller: MappingHistoryCtrl, templateUrl: '../../moduleResources/relationshipmapping/partials/mappingHistory.html'}).

            otherwise({redirectTo: '/mappingHistory'});

        $translateProvider.useSanitizeValueStrategy('sanitizeParameters');
        $translateProvider.useStaticFilesLoader({
              prefix: '../../moduleResources/relationshipmapping/languageResources/strings_',
              suffix: '.json'
            });
        $translateProvider.preferredLanguage('en');
        $translateProvider.fallbackLanguage('en');
    }]
);

relationshipMappingModule.factory('$data', function ($http) {});


relationshipMappingModule.factory('$localeService', function ($http) {
    var getUserLocale = function(){
        return $http.get("getUserLocale.json");
    };

    return {
        getUserLocale: getUserLocale
    }
});
relationshipMappingModule.factory('$data', function ($http) {
    var getExecutionCycles = function (search, pageNumber, pageSize) {
        if (search === undefined) {
            // replace undefined search term with empty string
            search = '';
        }
        return $http.get("executioncycles.json?search=" + search + "&pageNumber=" + pageNumber + "&pageSize=" + pageSize);
    };

    var runMappingTask = function(){
        return $http.get("runtasknow.json");
    }
    return {
        getExecutionCycles: getExecutionCycles,
        runMappingTask:runMappingTask
    }
});
