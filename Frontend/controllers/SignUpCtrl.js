'use strict';
var app = angular.module('app');

app.controller('SignUpController',
        function ($scope, $http, $rootScope, $state, $q, $cookies) {

            $scope.roles = [
                {name: "teacher"},
                {name: "student"},
            ];
            $scope.usernametaken = false;
            $scope.newuser = {};
            $scope.newuser.role = $scope.roles[1];

            // signup function
            $scope.signup = function () {
                var post_url = url + user_ep;
                var login_url = url + login_ep;
                var newuser = {
                    username: $scope.newuser.name,
                    password: $scope.newuser.password,
                    role: $scope.newuser.role.name,
                };
                $http.post(post_url, JSON.stringify(newuser))
                        .then(function (response) {
                            var login = {
                                username: $scope.newuser.name,
                                password: $scope.newuser.password,
                            };
                            // log in the new user
                            $http.post(login_url, JSON.stringify(login)).then(function (response) {
                                $scope.user = {
                                    username: response.data.username,
                                    role: response.data.role,
                                    userId: response.data.userId,
                                };

                                // get the user to get subscriptions
                                $http.get(url + user_ep + $scope.user.userId).then(function (response) {
                                    var sub = [];
                                    for (var i = 0; i < response.data.subscriptions.length; i++) {
                                        sub.push(response.data.subscriptions[i].courseId.toString());
                                    }
                                    ;
                                    $scope.user.sub = sub;

                                    // save user in rootscope and cookies
                                    $rootScope.user = $scope.user;
                                    $cookies.putObject("user", $scope.user);
                                    $state.go('stateCourses');
                                });
                            });
                        })
                        .catch(function (response) {
                            $scope.usernametaken = true;
                            $scope.newuser.password = '';
                            $scope.newuser.confirm_password = '';
                        });
            };

        });

