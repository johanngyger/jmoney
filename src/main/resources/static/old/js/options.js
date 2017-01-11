angular.module('options', ['ui.directives']).
    config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
        //$locationProvider.html5Mode(true);
        $routeProvider.
            when('/options/init', {controller: 'InitController', templateUrl: 'templates/options/init.html'}).
            when('/options/import', {controller: 'ImportController', templateUrl: 'templates/options/import.html'});
    }]);

function OptionsController($scope, $location) {
    $scope.$location = $location;
}

function InitController($scope, $http) {
    $scope.init = function () {
        $scope.loading = true;
        $http.put('/rest/options/init', {timeout: 5000})
            .success(function () {
                $scope.success = true;
                $scope.loading = false;
            })
            .error(function () {
                $scope.error = true;
                $scope.loading = false;
            });
    }
}

function ImportController($scope, $routeParams) {
    $scope.success = $routeParams.success;
    $scope.error = $routeParams.error;

    $scope.load = function () {
        $scope.loading = true;
    }
}

function ExportController($scope, $location) {

}

