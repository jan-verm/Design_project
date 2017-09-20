'use strict';
var app = angular.module('app');

app.controller('BrowseCoursesController',
        function ($scope, $rootScope, $cookies, $http) {
            $scope.courses = [];
            $scope.sublist = [];
            $scope.addCourseForm = false;

            // get all courses
            var courses_req = $http.get(url + course_ep);
            courses_req.then(function (values) {
                for (var i = 0; i < values.data.length; i++) {
                    $scope.courses.push(values.data[i]);
                }
            });

            // get user subscription list
            var user_req = $http.get(url + user_ep + $cookies.getObject("user").userId).then(function (response) {
                $scope.sublist = response.data.subscriptions;
            });

            // add course functionality
            $scope.coursenametaken = false;
            $scope.addCourse = function () {
                var add_course = {
                    name: $scope.new_course.name,
                };
                var posturl = url + course_ep;
                $http.post(posturl, JSON.stringify(add_course))
                        .then(function (response) {
                            $scope.courses.push(response.data);
                            $scope.addCourseForm = false;
                            $scope.new_course.name = "";
                        })
                        .catch(function (response) {
                            $scope.coursenametaken = true;
                        });
            };

            // check if current user is owner of a course
            $scope.isOwner = function (id) {
                var ret = false;
                for (var i = 0; i < $scope.courses.length; i++) {
                    if ($scope.courses[i].courseId == id) {
                        if ($scope.courses[i].owner == $cookies.getObject("user").username) {
                            ret = true;
                            break;
                        }
                    }
                }
                return ret;
            };

            // check if current user is subscribed to a course
            $scope.isSubscribed = function (id) {
                var ret = false;
                for (var i = 0; i < $scope.sublist.length; i++) {
                    if ($scope.sublist[i].courseId == id) {
                        ret = true;
                        break;
                    }
                }
                return ret;
            };

            // subscribe to a course
            $scope.subscribe = function (id) {
                var suburl = url + course_ep + id + '/' + subscribe_ep;
                for (var i = 0; i < $scope.courses.length; i++) {
                    if ($scope.courses[i].courseId == id)
                        $scope.sublist.push($scope.courses[i]);
                }
                $http.post(suburl);

                // update rootscope and user cookies
                $rootScope.user.sub.push(id.toString());
                $cookies.putObject("user", $rootScope.user);
            };

            // unsubscribe from a course
            $scope.unsubscribe = function (id) {
                var unsuburl = url + course_ep + id + '/' + unsubscribe_ep;
                for (var i = 0; i < $scope.sublist.length; i++) {
                    if ($scope.sublist[i].courseId == id)
                        $scope.sublist.splice(i, 1);
                }
                $http.post(unsuburl);

                // update rootscope and user cookies
                $rootScope.user.sub.splice($rootScope.user.sub.indexOf(id.toString()), 1);
                $cookies.putObject("user", $rootScope.user);
            };
        }
);

