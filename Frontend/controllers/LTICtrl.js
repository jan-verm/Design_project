'use strict';
var app = angular.module('app');

app.controller('LTIController',
        function ($scope, $rootScope, $http, $state, $cookies, $stateParams) {
            $scope.user = {
            	username: $stateParams.username,
                role: $stateParams.role,
                userId: $stateParams.id
            };
            
            // get the user to get subscriptions
            $http.get(url + user_ep + $scope.user.userId).then( function (response) {
                var sub = [];
                for (var i = 0; i < response.data.subscriptions.length; i ++) {
                    sub.push(response.data.subscriptions[i].courseId.toString());
                };
                $scope.user.sub = sub;
                
                // save user in rootscope and cookies
                $rootScope.user = $scope.user;
                $cookies.putObject("user", $scope.user);
                $state.go('stateCourses');
            });
        });
        