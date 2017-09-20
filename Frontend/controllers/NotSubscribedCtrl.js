'use strict';
var app = angular.module('app');

app.controller('NotSubscribedController',
        function ($scope, $rootScope, $state, $stateParams, $cookies, $http, $q, $timeout) {
            $scope.courseId = $stateParams.courseId;
            var courseurl = url + course_ep + $scope.courseId;
            
            // get course the user is trying to access
            $http.get(courseurl)
                    .then(function (response) {
                        $scope.course = response.data;
                    });

            // subscribe to the course
            $scope.subscribe = function () {
                var suburl = url + course_ep + $scope.courseId + '/' + subscribe_ep;
                $http.post(suburl);
                
                // update user cookies
                $rootScope.user.sub.push($scope.courseId.toString());
                $cookies.putObject("user", $rootScope.user);

                // update course, lecture, notes and video cookies with the new course
                var courses = $cookies.getObject("courses");
                courses.push({'name': $scope.course.name, 'courseId': $scope.courseId});
                $cookies.putObject("courses", courses);

                var vid_reqs = [];
                var note_reqs = [];
                $scope.videos = [];
                $scope.notes = [];
                $scope.lectures = [];

                for (var i = 0; i < $scope.course.videos.length; i++) {
                    $scope.videos.push({'name': $scope.course.videos[i].name, 'videoId': $scope.course.videos[i].videoId});
                }
                for (var i = 0; i < $scope.course.courseNotes.length; i++) {
                    $scope.notes.push({'name': $scope.course.courseNotes[i].name, 'courseNotesId': $scope.course.courseNotes[i].courseNotesId});
                }
                for (var i = 0; i < $scope.course.lectures.length; i++) {
                    $scope.lectures.push({'name': $scope.course.lectures[i].name, 'lectureId': $scope.course.lectures[i].lectureId});
                    var vids = $http.get(url + course_ep + $scope.courseId + '/' + lecture_ep + $scope.course.lectures[i].lectureId + '/' + video_ep);
                    var notes = $http.get(url + course_ep + $scope.courseId + '/' + lecture_ep + $scope.course.lectures[i].lectureId + '/' + notes_ep);
                    vid_reqs.push(vids);
                    note_reqs.push(notes);
                }

                $q.all(vid_reqs).then(function (values) {
                    for (var i = 0; i < values.length; i++) {
                        for (var j = 0; j < values[i].data.length; j++) {
                            $scope.videos.push({'name': values[i].data[j].name, 'videoId': values[i].data[j].videoId});
                        };
                    };
                    $cookies.putObject('videos', $scope.videos);
                });

                $q.all(note_reqs).then(function (values) {
                    for (var i = 0; i < values.length; i++) {
                        for (var j = 0; j < values[i].data.length; j++) {
                            $scope.notes.push({'name': values[i].data[j].name, 'courseNotesId': values[i].data[j].courseNotesId});
                        };
                    };
                    $cookies.putObject('notes', $scope.notes);
                });

                $cookies.putObject('lectures', $scope.lectures);

                // route to the new page
                // small timeout to wait for cookies and requests to be done
                $timeout(function () {
                    if (!$stateParams.linkstate) {
                        $state.go('stateCourse', {courseId: $scope.courseId});
                    } else if ($stateParams.linkstate.notesId) {
                        if ($stateParams.linkstate.lectureId) {
                            $state.go('stateCourseNotes', {courseId: $scope.courseId, lectureId: $stateParams.linkstate.lectureId,
                                notesId: $stateParams.linkstate.notesId, com: $stateParams.linkstate.com});
                        } else {
                            $state.go('stateCourseCourseNotes', {courseId: $scope.courseId,
                                notesId: $stateParams.linkstate.notesId, com: $stateParams.linkstate.com});
                        }
                    } else if ($stateParams.linkstate.videoId) {
                        if ($stateParams.linkstate.lectureId) {
                            $state.go('stateVideo', {courseId: $scope.courseId, lectureId: $stateParams.linkstate.lectureId,
                                videoId: $stateParams.linkstate.videoId, t: $stateParams.linkstate.t});
                        } else {
                            $state.go('stateCourseVideo', {courseId: $scope.courseId,
                                videoId: $stateParams.linkstate.videoId, t: $stateParams.linkstate.t});
                        }
                    }
                }, 300);
            };

        });