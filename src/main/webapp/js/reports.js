angular.module('reports', ['ui.directives']).
    config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
        //$locationProvider.html5Mode(true);
        $routeProvider.
            when('/reports/balances', {controller: 'BalancesController', templateUrl: 'templates/reports/balances.html'}).
            when('/reports/cash-flows', {controller: 'CashFlowController', templateUrl: 'templates/reports/cash-flows.html'});
    }]);

function ReportsController($scope, $location) {
    $scope.$location = $location;
}

function BalancesController($scope, $http, $filter) {
    $scope.loading = true;
    $scope.generate = function () {
        var dateParam = $filter('date')($scope.filterDate, 'yyyy-MM-dd');
        $http.get('rest/reports/balances', {params: {date: dateParam}})
            .success(function (data) {
                $scope.balances = data;
                $scope.loading = false;
            }).error(function () {
                $scope.error = true;
                $scope.loading = false;
            });
    }

    $scope.filter = "date";
    $scope.filterDate = null;
    $scope.generate();
}

function CashFlowController($scope, $http, $filter) {
    $scope.generate = function () {
        $scope.cashFlows = null;
        $scope.loading = true;
        var fromDateParam = $filter('date')($scope.fromDate, 'yyyy-MM-dd');
        var toDateParam = $filter('date')($scope.toDate, 'yyyy-MM-dd');
        $http.get('rest/reports/cash-flows', {params: {fromDate: fromDateParam, toDate: toDateParam}})
            .success(function (data) {
                $scope.cashFlows = data;
                $scope.loading = false;
            })
            .error(function () {
                $scope.error = true;
                $scope.loading = false;
            });
    }

    $scope.periods = [
        {value: 'thisMonth', description: 'Dieser Monat'},
        {value: 'thisYear', description: 'Dieses Jahr'},
        {value: 'lastMonth', description: 'Letzer Monat'},
        {value: 'lastYear', description: 'Letztes Jahr'}
    ];

    $scope.updatePeriod = function () {
        var today = new Date();
        var year = today.getFullYear();
        var month = today.getMonth();
        var period = $scope.period;
        switch (period) {
            case "thisMonth":
                $scope.fromDate = new Date(year, month, 1);
                $scope.toDate = new Date(year, month + 1, 0);
                break;
            case "thisYear":
                $scope.fromDate = new Date(year, 0, 1);
                $scope.toDate = new Date(year, 11, 31);
                break;
            case "lastMonth":
                $scope.fromDate = new Date(year, month - 1, 1);
                $scope.toDate = new Date(year, month, 0);
                break;
            case "lastYear":
                $scope.fromDate = new Date(year - 1, 0, 1);
                $scope.toDate = new Date(year - 1, 11, 31);
                break
        }
    };

    $scope.period = "thisMonth";
    $scope.updatePeriod();
    $scope.generate();
}



