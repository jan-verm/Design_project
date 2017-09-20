'use strict';
var app = angular.module('app');

app.controller('LoginController',
        function ($scope, $http, $rootScope, $state, $cookies) {

            var login_url = url + login_ep;
            $scope.wronglogin = false;
            
            // login function
            $scope.login = function () {
                var newuser = {
                    username: $scope.user.name,
                    password: $scope.user.password
                };
                
                $http.post(login_url, JSON.stringify(newuser))
                        .then(function (response) {
                            $scope.user = {
                                username: response.data.username,
                                role: response.data.role,
                                userId: response.data.userId,
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
                            
                        })
                        .catch( function (response) {
                            $scope.user.password = '';
                            $scope.wronglogin = true;
                        });
            };

        });
        
