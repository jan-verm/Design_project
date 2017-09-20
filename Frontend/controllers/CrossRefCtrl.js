'use strict';
var app = angular.module('app');

app.controller('CrossRefController',
        function ($scope, $rootScope, $http, $q, $sce, $uibModalInstance, comm_index, reply_index, reply2reply_index, $controller) {
            $.extend(this, $controller('ViewerController', {$scope: $scope}));

            var short_url = url + course_ep + $scope.courseId + '/' + lecture_ep + $scope.lectureId + '/';
            var course_url = url + course_ep;

            $scope.crossrefctrl = this;
            $scope.crossrefctrl.API = null;

            $scope.crossrefctrl.onPlayerReady = function (API) {
                $scope.crossrefctrl.API = API;
            };
            
            // initialize timepoint on 00:00:00
            $scope.crtime = {
                hours: '00',
                minutes: '00',
                seconds: '00',
            };

            // get course and user
            var course_req = $http.get(course_url);
            var user_req = $http.get(url + user_ep + $rootScope.user.userId);

            $q.all([user_req, course_req]).then(function (values) {
                var sublist = values[0].data.subscriptions;
                $scope.cr_courses = [];
                // show only subscribed courses
                for (var i = 0; i < values[1].data.length; i++) {
                    if (values[1].data[i].owner == $rootScope.user.username) {
                        var course = values[1].data[i];
                        $scope.cr_courses.push(course);
                    } else {
                        for (var j = 0; j < sublist.length; j++) {
                            if (values[1].data[i].courseId == sublist[j].courseId) {
                                $scope.cr_courses.push(values[1].data[i]);
                            }
                        }
                    }
                }
                
                // init selected course on the first course
                $scope.selected_course = $scope.cr_courses[0];
                $scope.getLectures($scope.cr_courses[0].courseId);
            });
            
            // get all lectures from a course
            $scope.getLectures = function (courseId) {
                var lecture_req = $http.get(course_url + courseId + '/' + lecture_ep);
                $q.all([lecture_req]).then(function (values) {
                    var lec0 = angular.copy($scope.selected_course);
                    lec0.lectureId = '0';
                    lec0.name = '(No lecture)';
                    $scope.cr_lectures = [lec0];
                    for (var i = 0; i < values[0].data.length; i++) {
                        $scope.cr_lectures.push(values[0].data[i]);
                    }
                    $scope.selected_lecture = $scope.cr_lectures[0];
                    $scope.getFiles(courseId, $scope.cr_lectures[0].lectureId, false);
                });
            };

            $scope.getCurrentLectures = function () {
                var lecture_req = $http.get(course_url + $scope.courseId + '/' + lecture_ep);
                $q.all([lecture_req]).then(function (values) {
                    // no lecture (course files)
                    var lec0 = angular.copy($scope.selected_course);
                    lec0.lectureId = '0';
                    lec0.name = '(No lecture)';
                    $scope.cr_lectures = [lec0];
                    
                    // lectures
                    for (var i = 0; i < values[0].data.length; i++) {
                        $scope.cr_lectures.push(values[0].data[i]);
                    }
                    for (var i = 0; i < $scope.cr_lectures.length; i++) {
                        if ($scope.cr_lectures[i].lectureId == $scope.lectureId) {
                            $scope.selected_lecture = $scope.cr_lectures[i];
                        }
                    }
                    ;
                });
            };

            // get all files in a lecture
            $scope.getFiles = function (courseId, lectureId, current) {
                var get_url = url + course_ep + courseId + '/' + lecture_ep + lectureId + '/';
                var vid_req = $http.get(get_url + video_ep);
                var notes_req = $http.get(get_url + notes_ep);

                $q.all([vid_req, notes_req]).then(function (values) {
                    //get all files
                    $scope.all_videos = values[0].data;
                    $scope.all_notes = values[1].data;
                    // add bool for easy separating of video and notes
                    for (var i = 0; i < $scope.all_notes.length; i++) {
                        $scope.all_notes[i].isNotes = true;
                    }
                    for (var i = 0; i < $scope.all_videos.length; i++) {
                        $scope.all_videos[i].isVideo = true;
                    }
                    //concat all files in one list
                    $scope.files = $scope.all_videos.concat($scope.all_notes);
                    if (current) {
                        selectCurrentFile();
                    }
                });
            };

            // select a file
            $scope.selectFile = function (file) {
                if ($scope.selected_file === file) {
                    $scope.selected_file = '';
                } else {
                    $scope.selected_file = file;
                    if ($scope.selected_file.isVideo) {
                        // if the file is a video, build the player
                        $scope.buildPlayer($scope.selected_file);
                    } else if ($scope.selected_file.isNotes) {
                        // if the file is a pdf, load PDF into viewer
                        window.initViewer($scope.selected_file, "crossrefViewer", []);
                    }
                }
            };

            // get the current file
            $scope.currentFile = function () {
                for (var i = 0; i < $scope.cr_courses.length; i++) {
                    if ($scope.cr_courses[i].courseId == $scope.courseId) {
                        $scope.selected_course = $scope.cr_courses[i];
                    }
                };

                $scope.getCurrentLectures();
                $scope.getFiles($scope.courseId, $scope.lectureId, true);
            };

            // select the current file
            var selectCurrentFile = function () {
                var vid = false;
                var id = $scope.notesId;
                var files = $scope.files;
                if ($scope.videoId) {
                    vid = true;
                    id = $scope.videoId;
                }

                for (var i = 0; i < files.length; i++) {
                    if (vid && files[i].videoId == id) {
                        $scope.selectFile(files[i]);
                    }
                    if (!(vid) && files[i].courseNotesId == id) {
                        $scope.selectFile(files[i]);
                    }
                };
            };

            // get current time on a video
            $scope.currentTime = function () {
                var current_time = secondsToHMS($scope.crossrefctrl.API.currentTime / 1000);
                $scope.crtime.hours = addZero(current_time[0]);
                $scope.crtime.minutes = addZero(current_time[1]);
                $scope.crtime.seconds = addZero(current_time[2]);
            };

            // build a video player
            $scope.buildPlayer = function (video) {
                $scope.crossrefctrl.media = [
                    {
                        sources: [{src: $sce.trustAsResourceUrl(video.url)}]
                    }
                ];

                $scope.crossrefctrl.config = {
                    playsInline: false,
                    autoHide: false,
                    autoHideTime: 3000,
                    autoPlay: false,
                    sources: $scope.crossrefctrl.media[0].sources,
                    loop: false,
                    preload: "auto",
                    controls: false,
                    theme: {
                        url: "http://www.videogular.com/styles/themes/default/latest/videogular.css"
                    },
                    cuePoints: {
                        console: $scope.timepoints,
                    },
                };
            };

            // add a cross-ref to a video
            $scope.vidCrossRef = function () {
                if (reply_index == -1) {
                    // add crossref to comment
                    var commentId = $scope.comments[comm_index].commentId;
                    var newvideoref = {
                        videoId: $scope.selected_file.videoId,
                        timestamp: HMStoSeconds($scope.crtime.hours, $scope.crtime.minutes, $scope.crtime.seconds),
                        duration: 0,
                    };
                    var posturl = short_url + comm_ep + commentId + '/' + videoref_ep;

                    $http.post(posturl, JSON.stringify(newvideoref))
                            .then(function (response) {
                                $scope.comments[comm_index].videorefs.push(response.data);

                                if ($scope.videoId == response.data.videoId) {
                                    var newlist = angular.copy($scope.timepoints);
                                    var timepoint = {
                                        timeLapse: {
                                            start: response.data.timestamp,
                                            end: response.data.timestamp,
                                        },
                                        params: {
                                            comment: $scope.comments[comm_index],
                                            isPopoverVisible: false,
                                            cuepointVisible: true,
                                        },
                                        onEnter: $scope.onEnter,
                                        onComplete: $scope.onComplete,
                                    };
                                    newlist.push(timepoint);
                                    $scope.timepoints = newlist;
                                    $scope.vidctrl.config.cuePoints.console = $scope.timepoints;
                                }
                                $uibModalInstance.close();
                            });
                            
                } 
                
                else if (reply2reply_index == -1) {
                    // add crossref to reply
                    var replyId = $scope.comments[comm_index].replies[reply_index].replyId;
                    var newvideoref = {
                        videoId: $scope.selected_file.videoId,
                        timestamp: HMStoSeconds($scope.crtime.hours, $scope.crtime.minutes, $scope.crtime.seconds),
                        duration: 0,
                    };
                    var posturl = url + reply_ep + replyId + '/' + videoref_ep;

                    $http.post(posturl, JSON.stringify(newvideoref))
                            .then(function (response) {
                                $scope.comments[comm_index].replies[reply_index].videorefs.push(response.data);
                                $uibModalInstance.close();
                            });
                } 
                
                else {
                    // add crossref to reply on reply
                    var replyId = $scope.comments[comm_index].replies[reply_index].replies[reply2reply_index].replyId;
                    var newvideoref = {
                        videoId: $scope.selected_file.videoId,
                        timestamp: HMStoSeconds($scope.crtime.hours, $scope.crtime.minutes, $scope.crtime.seconds),
                        duration: 0,
                    };
                    var posturl = url + reply_ep + replyId + '/' + videoref_ep;

                    $http.post(posturl, JSON.stringify(newvideoref))
                            .then(function (response) {
                                $scope.comments[comm_index].replies[reply_index].replies[reply2reply_index].videorefs.push(response.data);
                                $uibModalInstance.close();
                            });
                }

            };

            // highlight in pdf
            $scope.crHighlight = [];
            $scope.selected = false;
            $scope.selectcrText = function (alert) {
                $scope.crToAdd = null;
                $scope.crToAdd = document.getElementById("crossrefViewer").contentWindow.highlightAll($scope.crHighlight, 'green');
                if ($scope.crToAdd.length === 0 || $scope.crToAdd[0].coords[0] === $scope.crToAdd[0].coords[2]) {
                    if (alert) {
                        window.alert("Please select some text in the file.")
                    }
                    $scope.crToAdd = null;
                }
                if ($scope.crToAdd) {
                    $scope.selected = true;
                }
            };

            // add a cross-ref to coursenotes
            $scope.notesCrossRef = function () {
                if (reply_index == -1) {
                    // add crossref to comment
                    var commentId = $scope.comments[comm_index].commentId;
                    var locations = [];
                    for (var i = 0; i < $scope.crToAdd.length; i++) {
                        locations.push({pageNumber: parseInt($scope.crToAdd[i].pageIndex) + 1,
                            x1: $scope.crToAdd[i].coords[0],
                            y1: $scope.crToAdd[i].coords[1],
                            x2: $scope.crToAdd[i].coords[2],
                            y2: $scope.crToAdd[i].coords[3]
                        });
                    }

                    var newcourseref = {
                        courseNotesId: $scope.selected_file.courseNotesId,
                        locations: locations,
                    };

                    var posturl = short_url + comm_ep + commentId + '/' + courseref_ep;
                    $http.post(posturl, JSON.stringify(newcourseref))
                            .then(function (response) {
                                $scope.comments[comm_index].noterefs.push(response.data);
                                $uibModalInstance.close();
                                // Recalculate and redraw the highlights.
                                $scope.reformatHighlights();
                                document.getElementById("classicViewer").contentWindow.highlightUpdateAll($scope.allHighlights);
                            });
                } 
                
                else if (reply2reply_index == -1) {
                    // add crossref to reply
                    var replyId = $scope.comments[comm_index].replies[reply_index].replyId;
                    var locations = [];
                    for (var i = 0; i < $scope.crToAdd.length; i++) {
                        locations.push({pageNumber: parseInt($scope.crToAdd[i].pageIndex) + 1,
                            x1: $scope.crToAdd[i].coords[0],
                            y1: $scope.crToAdd[i].coords[1],
                            x2: $scope.crToAdd[i].coords[2],
                            y2: $scope.crToAdd[i].coords[3]
                        });
                    }

                    var newcourseref = {
                        courseNotesId: $scope.selected_file.courseNotesId,
                        locations: locations,
                    };

                    var posturl = url + reply_ep + replyId + '/' + courseref_ep;
                    $http.post(posturl, JSON.stringify(newcourseref))
                            .then(function succesCallback(response) {
                                $scope.comments[comm_index].replies[reply_index].noterefs.push(response.data);
                                $uibModalInstance.close();
                            }, function errorCallback(response){
                                if(response.status == '400'){
                                    // save locally to avoid page refresh to see crossref (after page refresh crossref comes from db)
                                    $scope.comments[comm_index].replies[reply_index].noterefs.push(newcourseref);
                                    $uibModalInstance.close();
                                }
                                
                            });

                } 
                
                else {
                    // add crossref to reply on reply
                    var replyId = $scope.comments[comm_index].replies[reply_index].replies[reply2reply_index].replyId;
                    var locations = [];
                    for (var i = 0; i < $scope.crToAdd.length; i++) {
                        locations.push({pageNumber: parseInt($scope.crToAdd[i].pageIndex) + 1,
                            x1: $scope.crToAdd[i].coords[0],
                            y1: $scope.crToAdd[i].coords[1],
                            x2: $scope.crToAdd[i].coords[2],
                            y2: $scope.crToAdd[i].coords[3]
                        });
                    }

                    var newcourseref = {
                        courseNotesId: $scope.selected_file.courseNotesId,
                        locations: locations,
                    };

                    var posturl = url + reply_ep + replyId + '/' + courseref_ep;
                    $http.post(posturl, JSON.stringify(newcourseref))
                            .then(function successCallback(response) {
                                $scope.comments[comm_index].replies[reply_index].replies[reply2reply_index].noterefs.push(response.data);
                                $uibModalInstance.close();
                            }, function errorCallback(response){
                                if(response.status == '400'){
                                    // save locally to avoid page refresh to see crossref (after page refresh crossref comes from db)
                                    $scope.comments[comm_index].replies[reply_index].replies[reply2reply_index].noterefs.push(newcourseref);
                                    $uibModalInstance.close();
                                }
                                
                            });
                }
            };
        });
