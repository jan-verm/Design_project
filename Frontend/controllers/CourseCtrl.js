'use strict';
var app = angular.module('app');

// get lecture name for breadcrumb nav
app.service('lectureService', function ($cookies) {
    this.getLectureName = function (id) {
        var lectures = $cookies.getObject('lectures');
        for (var i = 0; i < lectures.length; i++) {
            if (lectures[i].lectureId == id) {
                return lectures[i].name;
            }
        }
    };
});

// get course name for breadcrumb nav
app.service('courseService', function ($cookies) {
    this.getCourseName = function (id) {
        var courses = $cookies.getObject('courses');
        for (var i = 0; i < courses.length; i++) {
            if (courses[i].courseId == id) {
                return courses[i].name;
            }
        }
    };
});

app.controller('CourseController',
        function ($scope, $rootScope, $http, $q, $state, $stateParams) {
            $scope.courseId = $stateParams.courseId;

            if ($rootScope.user.sub.indexOf($scope.courseId) == -1) {
                // user is not subscribed to this course
                $state.go('stateNotSubscribed', {courseId: $scope.courseId});
            }
            ;

            $scope.type = "course";
            var course_url = url + course_ep + $scope.courseId + '/';
            var course_req = $http.get(course_url);

            // if this is a lecture, change variables accordingly
            if ($stateParams.lectureId) {
                $scope.lectureId = $stateParams.lectureId;
                $scope.type = "lecture";
                course_req = $http.get(course_url + lecture_ep + $scope.lectureId + '/');
            } else {
                $scope.lectureId = '0';
            }
            var full_url = url + course_ep + $scope.courseId + '/' + lecture_ep + $scope.lectureId + '/';


            $scope.lectures = [];
            $scope.videos = [];
            $scope.notes = [];

            $scope.videoForm = false;
            $scope.lectureForm = false;
            $scope.editName = false;

            // fill variables from REST service
            $q.all([course_req]).then(function (values) {
                $scope.courseName = values[0].data.name;
                $scope.lectures = values[0].data.lectures;
                $scope.videos = values[0].data.videos;

                // get video thumbnail
                for (var i = 0; i < $scope.videos.length; i++) {
                    $scope.videos[i].thumbnail = 'http://img.youtube.com/vi/' + getYoutubeId($scope.videos[i].url) + '/mqdefault.jpg';
                }
                $scope.notes = values[0].data.courseNotes;
                $scope.isOwner = (values[0].data.owner == $rootScope.user.username || $rootScope.user.role == 'ADMIN');
            });

            // add a lecture
            $scope.addLecture = function () {
                var add_lecture = {
                    name: $scope.new_lecture.name,
                };
                var posturl = course_url + lecture_ep;
                $http.post(posturl, JSON.stringify(add_lecture))
                        .then(function (response) {
                            $scope.lectures.push(response.data);
                            $scope.lectureForm = false;
                            $scope.new_lecture.name = "";
                        });
            };

            // add a video
            $scope.submitVideo = function () {
                var posturl = full_url + video_ep;

                // retrieve video information
                // key to acces the Youtube API: AIzaSyBtJIMKyKE5LJ3LhGHbvYyq8oceoyk6V5k
                var request = $http.get("https://www.googleapis.com/youtube/v3/videos?id=" + getYoutubeId($scope.new_video.url) + "&key=AIzaSyBtJIMKyKE5LJ3LhGHbvYyq8oceoyk6V5k&part=snippet,contentDetails")
                        .then(function (response) {
                            $scope.name = response.data.items[0].snippet.title;
                            $scope.duration = toSeconds(String(response.data.items[0].contentDetails.duration));
                        });

                $q.all([request]).then(function (values) {
                    var video_json = {
                        name: $scope.name,
                        url: $scope.new_video.url,
                        duration: $scope.duration,
                    };

                    $http.post(posturl, JSON.stringify(video_json))
                            .success(function (data) {
                                data.thumbnail = 'http://img.youtube.com/vi/' + getYoutubeId(data.url) + '/mqdefault.jpg';
                                $scope.videos.push(data);
                                $scope.videoForm = false;
                                $scope.new_video = {};
                            });
                });
            };

            // upload a pdf
            $scope.files = [];
            $scope.validInput = false;
            $scope.submitPDF = function () {
                var uploadurl = full_url + upload_ep;
                if ($scope.files.length === 0) {
                    $scope.validInput = false;
                } else {
                    $scope.files.forEach(function (file) {
                        $scope.validInput = true;
                        var fd = new FormData();
                        //Take the first selected file
                        fd.append("file", file);
                        fd.append("name", file.name);
                        fd.append("course", $scope.courseId);
                        fd.append("lecture", $scope.lectureId);

                        $http.post(uploadurl, fd, {withCredentials: true, headers: {'Content-Type': undefined}, transformRequest: angular.identity})
                                .then(function (response) {
                                    $scope.notes.push(response.data);
                                    $scope.coursenotesForm = false;
                                });
                    });
                }
            };

            // Hide the upload-notes-form and reset its content.
            $scope.closePDF = function (form) {
                $scope.coursenotesForm = !$scope.coursenotesForm;
                $scope.files = [];
                form.$setPristine();
                form.$setUntouched();
            };

            // Add (or not) the newly selected file to a list to be uploaded.
            $scope.uploadFile = function (event) {
                if (event.target.files[0] !== undefined) {
                    var unique = true;
                    $scope.validInput = true;
                    // Check if the file is not already in the list.
                    $scope.files.forEach(function (file) {
                        if (file.name === event.target.files[0].name) {
                            unique = false;
                            return;
                        }
                    });
                    // Only add the file to the list if is not already in the list.
                    if (unique) {
                        $scope.files.push(event.target.files[0]);
                    }
                    $scope.$apply();
                }
            };

            $scope.removeFile = function (index) {
                $scope.files.splice(index, 1);
            };


            // change course name
            $scope.changeName = function () {
                var patchurl = full_url;
                var id = $scope.lectureId;
                if ($scope.type == 'course') {
                    patchurl = course_url;
                    id = $scope.courseId;
                }

                var patched_course = {
                    courseId: id,
                    name: $scope.courseName,
                    videos: $scope.videos,
                    lectures: $scope.lectures,
                    courseNotes: $scope.notes
                };

                $http.patch(patchurl, JSON.stringify(patched_course))
                        .then(function (response) {
                            $scope.editName = false;
                        });
                $scope.editName = false;
            };


            // delete video
            $scope.deleteVideo = function (index) {
                // send DELETE request to backend
                var delete_url = full_url + video_ep + index;
                $http.delete(delete_url)
                        .then(function () {
                            //also delete from local list
                            for (var i = 0; i < $scope.videos.length; i++) {
                                if ($scope.videos[i].videoId === index) {
                                    $scope.videos.splice(i, 1);
                                }
                            }
                        });
            };

            // delete pdf
            $scope.deleteNotes = function (index) {
                // send DELETE request to backend
                var delete_url = full_url + notes_ep + index;
                $http.delete(delete_url)
                        .then(function () {
                            //also delete from local list
                            for (var i = 0; i < $scope.notes.length; i++) {
                                if ($scope.notes[i].courseNotesId === index) {
                                    $scope.notes.splice(i, 1);
                                }
                            }
                        });
            };

            // delete lecture
            $scope.deleteLecture = function (index) {
                var delete_url = course_url + lecture_ep + index;
                $http.delete(delete_url)
                        .then(function () {
                            //also delete from local list
                            for (var i = 0; i < $scope.lectures.length; i++) {
                                if ($scope.lectures[i].lectureId === index) {
                                    $scope.lectures.splice(i, 1);
                                }
                            }
                        });
            };

            // delete the entire course
            $scope.deleteCourse = function () {
                // check if course or lecture
                var delete_url = '';
                if ($scope.type == 'course') {
                    delete_url = course_url;
                } else {
                    delete_url = full_url;
                }
                $http.delete(delete_url)
                        .then(function () {
                            $state.go('^');
                        });
            };

            $scope.checkStateCondition = function (type, respectiveIdString) {
                if ($scope.type === 'course') {
                    return 'stateCourse' + type + '({courseId:' + $scope.courseId + ', ' + respectiveIdString + '})';
                } else {
                    return 'state' + type + '({courseId:' + $scope.courseId + ', ' + respectiveIdString + '})';
                }
            };

        });

app.directive('customOnChange', function () {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var onChangeHandler = scope.$eval(attrs.customOnChange);
            element.bind('change', onChangeHandler);
        }
    };
});