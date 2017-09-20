'use strict';
var app = angular.module('app');

app.controller('AccountController',
        function ($rootScope, $scope, $http, $state, $cookies) {

            // logout function
            $scope.logout = function () {
                var logout_url = url + logout_ep;
                $http.post(logout_url).then(function () {
                    $rootScope.user = null;
                    $cookies.remove("user");
                    $state.go('stateLogin');
                });
            };

            // LTI init function
            $scope.init = function () {
                $scope.key = '';
                $scope.secret = '';
                $scope.createKey = false;
                $scope.correctKey = true;
                $scope.succes = false;
            };

            $scope.generateSecret = function () {
                $scope.correctKey = true
                // post key to request secret
                var post_url = url + ltikeypair_ep
                var key_json = {"key": $scope.key}
                $http.post(post_url, JSON.stringify(key_json))
                        .then(function successCallback(response) {
                            $scope.secret = response.data.secret;
                            $scope.key = response.data.key;
                            $scope.succes = true;
                        }, function errorCallback(response) {
                            $scope.correctKey = false;
                        });
            };


            // delete message for popup
            $scope.confirmdelete = "Are you sure you want to delete your account?" + '\n' + "All your courses, comments and replies will also be deleted!";

            // delete account
            $scope.deleteAccount = function () {
                var delete_url = url + user_ep + $rootScope.user.userId;
                $http.delete(delete_url).then(function () {
                    $rootScope.user = null;
                    $cookies.remove("user");
                    $state.go('stateHome');
                });
            };

        });
        