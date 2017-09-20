'use strict'
var app = angular.module('app');

// handle unauthorized user (401)
// (usually because of a timeout)
app.controller('UnauthController', function ($scope, $rootScope, $cookies, $stateParams, $http) {
    $scope.errmsg = $stateParams.errmsg;
    if ($scope.errmsg.indexOf("Not logged in!") > 1){
        var logout_url = url + logout_ep;
                $http.post(logout_url).then(function () {
                    $rootScope.user = null;
                    $cookies.remove("user");
                });
    }
});

// handle internal server error (500)
app.controller('InternalErrController', function ($scope, $stateParams) {
    $scope.errmsg = $stateParams.errmsg;
});

