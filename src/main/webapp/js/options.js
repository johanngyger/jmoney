angular.module('options', ['ui.directives']).
    config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
        //$locationProvider.html5Mode(true);
        $routeProvider.
            when('/options/init', {controller: 'InitController', templateUrl: 'templates/options/init.html'}).
            when('/options/import', {controller: 'ImportController', templateUrl: 'templates/options/import.html'}).
            when('/options/export', {controller: 'ExportController', templateUrl: 'templates/options/export.html'});
    }]);

function OptionsController($scope, $location) {
    $scope.$location = $location;
}

function InitController($scope, $http) {
    $scope.init = function () {
        $scope.isLoading = true;
        $http.put('rest/options/init').success(function () {
            $scope.isLoading = false;
        });
    }
}

function ImportController($scope, $routeParams) {
    $scope.success = $routeParams.success;
    $scope.failure = $routeParams.failure;

    $scope.load = function () {
        $scope.isLoading = true;
    }
}

function ExportController($scope, $location) {

}

