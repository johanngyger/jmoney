angular.module('categories', ['ui.directives']).
    config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
        //$locationProvider.html5Mode(true);
        $routeProvider.
            otherwise({redirectTo: '/categories.html'});
    }]);

function CategoryTreeController($scope, $http, $routeParams, $rootScope) {

    var load = function () {
        $scope.loading = true;
        $scope.error = false;
        $http.get('/rest/category-tree')
            .success(function (data) {
                $scope.tree = data;
                $scope.loading = false;
            }).error(function (data) {
                $scope.error = true;
                $scope.loading = false;
            });
    }

    load();

    $scope.save = function () {
        $http.put('/rest/category-tree', $scope.tree)
            .success(function (data) {
                load();
            });
    }

    $scope.add = function (parent) {
        var newCat = {name: '<Neue Kategorie>', children: [], parentId: parent.id};
        $http.post('/rest/categories', newCat).success(function (data) {
            newCat.id = data;
        });
        parent.children.unshift(newCat);
    }

    $scope.remove = function (node) {
        removeNode(node, $scope.tree.children);
    }

    var removeNode = function (node, nodes) {
        for (var i = 0; i < nodes.length; i++) {
            var n = nodes[i];
            if (node == n) {
                nodes.splice(i, 1);
                $http.delete('/rest/categories/' + node.id);
                return;
            }
            removeNode(node, n.children);
        }
    }

}
