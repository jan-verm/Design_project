'use strict';
var app = angular.module('app');

app.controller('MyCoursesController',
        function ($scope, $rootScope, $http, $q, $cookies, $timeout) {
            $scope.courses = [];
            $scope.courseCookies = [];
            var courses_req = $http.get(url + course_ep);

            $q.all([courses_req]).then(function (values) {
                // only show user's subscribed an owned courses
                var sublist = $rootScope.user.sub;
                for (var i = 0; i < values[0].data.length; i++) {
                    if (values[0].data[i].owner == $rootScope.user.username) {
                        var course = values[0].data[i];
                        course.owned = true;
                        $scope.courses.push(course);
                        $scope.courseCookies.push({'name': values[0].data[i].name, 'courseId': values[0].data[i].courseId});
                        if ($rootScope.user.sub.indexOf(values[0].data[i].courseId.toString()) == -1) {
                            // in case the owned course isn't in the subbed list yet, add it
                            $rootScope.user.sub.push(values[0].data[i].courseId.toString());
                        }
                    } else {
                        for (var j = 0; j < sublist.length; j++) {
                            if (values[0].data[i].courseId == sublist[j]) {
                                var course = values[0].data[i];
                                course.owned = false;
                                $scope.courses.push(course);
                                // pur courses in cookies for breadcrumb nav
                                $scope.courseCookies.push({'name': values[0].data[i].name, 'courseId': values[0].data[i].courseId});
                            }
                        }
                    }
                }
                
                //update cookies with owned courses
                $cookies.putObject("user", $rootScope.user);
                
                // put all lectures, videos and notes (name + id) in cookies for breadcrumb nav
                $scope.lectures = [];
                $scope.videos = [];
                $scope.notes = [];
                var vid_reqs = [];
                var note_reqs = [];
                
                angular.forEach($scope.courses, function(course) {
                    for (var i = 0; i < course.videos.length; i++) {
                        $scope.videos.push({'name': course.videos[i].name, 'videoId': course.videos[i].videoId});
                    }
                    for (var i = 0; i < course.courseNotes.length; i++) {
                        $scope.notes.push({'name': course.courseNotes[i].name, 'courseNotesId': course.courseNotes[i].courseNotesId});
                    }
                    for (var i = 0; i < course.lectures.length; i++) {
                        $scope.lectures.push({'name': course.lectures[i].name, 'lectureId': course.lectures[i].lectureId});
                        var vids = $http.get(url + course_ep + course.courseId + '/' + lecture_ep + course.lectures[i].lectureId + '/' + video_ep);
                        var notes = $http.get(url + course_ep + course.courseId + '/' + lecture_ep + course.lectures[i].lectureId + '/' + notes_ep);
                        vid_reqs.push(vids);
                        note_reqs.push(notes);
                    }
                });
                
                $q.all(vid_reqs).then( function (values) {
                    for (var i = 0; i < values.length; i++ ){
                        for (var j = 0; j < values[i].data.length; j++) {
                            $scope.videos.push({'name': values[i].data[j].name, 'videoId': values[i].data[j].videoId});
                        };
                    };
                    $cookies.putObject('videos', $scope.videos);
                });
                
                $q.all(note_reqs).then( function (values) {
                    for (var i = 0; i < values.length; i++ ){
                        for (var j = 0; j < values[i].data.length; j++) {
                            $scope.notes.push({'name': values[i].data[j].name, 'courseNotesId': values[i].data[j].courseNotesId});
                        };
                    };
                    $cookies.putObject('notes', $scope.notes);
                });
                
                $cookies.putObject('lectures', $scope.lectures);
                $cookies.putObject('courses', $scope.courseCookies);
                
            });

        });

