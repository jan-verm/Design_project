'use strict';
var app = angular.module('app');

app.service('videoService', function ($cookies) {
    this.getVideoName = function (id) {
        var videos = $cookies.getObject('videos');
    	for( var i=0; i< videos.length; i++){
    		if(videos[i].videoId == id){
    			return videos[i].name;
    		}
    	}
    };
});

app.controller('VideoController',
        function ($scope, $rootScope, $http, $q, $sce, $stateParams, $timeout, $uibModal, $state, $anchorScroll, $controller, $location) {
            $.extend(this, $controller('CommentController', {$scope: $scope}));
            $scope.vidctrl = this;
            
            $scope.courseId = $stateParams.courseId;
            $rootScope.courseId = $stateParams.courseId;
            
            if($stateParams.lectureId) {
                $scope.lectureId = $stateParams.lectureId;
                $scope.type = "lecture";
            }
            else {
                $scope.lectureId = '0';
            }
            $scope.videoId = $stateParams.videoId;
            
            if ($rootScope.user.sub.indexOf($scope.courseId) == -1) {
                $state.go('stateNotSubscribed', {courseId: $scope.courseId, linkstate: $stateParams});
            };
            
            $scope.short_url = url + course_ep + $scope.courseId + '/' + lecture_ep + $scope.lectureId + '/';
            $scope.full_url = $scope.short_url + video_ep;
            $scope.course_material_url = $scope.full_url + $scope.videoId;
            
            $scope.linkto = 'views/partials/linkto_video.html';
            $scope.editform = 'views/partials/editvideocommentform.html';
            $scope.confirmdelete = "Are you sure you want to delete this comment? Deleting the comment will also delete all replies and cross-refrences."
            $anchorScroll.yOffset = 100;
            
            $scope.comments = [];
            $scope.video = {};

            $scope.vidctrl.API = null;
            $scope.vidctrl.barChartStyle = {};
            $scope.vidctrl.textStyle = {};

            $scope.vidctrl.onPlayerReady = function (API) {
                $scope.vidctrl.API = API;
            };

            var vid_req = $http.get($scope.full_url + $scope.videoId);
            var anns_req = $http.get($scope.full_url + $scope.videoId + '/' + comm_ep);
            var course_req = $http.get(url + course_ep + $scope.courseId);
            // defer requests; meaning the variables will be filled when the GET request is done
            $q.all([vid_req, anns_req, course_req]).then(function (values) {
                $scope.buildPlayer(values[0].data, values[1].data);
                $timeout(function() { 
                    if ($stateParams.t !== '0'){
                        $scope.vidctrl.API.seekTime(parseInt($stateParams.t));
                    }
                }, 1000);
                
                $scope.isOwner = ($rootScope.user.role == 'ADMIN' || values[2].data.owner == $rootScope.user.username);
            });

            //*********************//
            //  COMMENT FUNCTIONS  //
            //*********************//
            $scope.recent = {};
            
            $scope.openCommentForm = function () {
                $scope.showCommentForm = false;
                $scope.currentTime();
            };
            
            $scope.currentTime = function () {
                var current_time = secondsToHMS($scope.vidctrl.API.currentTime / 1000);
                if(current_time[0] == 0 && current_time[1] == 0 && current_time[2] == 0){
                    //do nothing so placeholders can fill in form
                }else{
                    $scope.new_comment.hours = addZero(current_time[0]);
                    $scope.new_comment.minutes = addZero(current_time[1]);
                    $scope.new_comment.seconds = addZero(current_time[2]);
                }
            };

            $scope.seekTo = function (timestamp, bool) {
                $location.hash("videoplayer");
                $anchorScroll();
                $scope.vidctrl.API.seekTime(timestamp, bool);
            };
    
            $scope.wrongtimestamp = function() {
                var stamp =  HMStoSeconds($scope.new_comment.hours, $scope.new_comment.minutes, $scope.new_comment.seconds);
                return (stamp > $scope.video.duration)
            }
            
            $scope.editName = false;
            $scope.changeName = function(video){
                var patch_url = $scope.short_url + video_ep + $scope.videoId;
                $http.patch(patch_url,video);
            };
            
            $scope.submitComment = function (form) {
                if($scope.wrongtimestamp() == false){
                    var posturl = $scope.full_url + $scope.videoId + '/' + comm_ep;
                    
                    if (!$scope.new_comment.question) {
                        $scope.new_comment.question = false;
                    }

                    var comment_json = {
                        username: $rootScope.user.username,
                        body: $scope.new_comment.text,
                        question: $scope.new_comment.question,
                        approved: false,
                        selfVideoReferences: [{
                            videoId: $scope.videoid,
                            timestamp: HMStoSeconds($scope.new_comment.hours, $scope.new_comment.minutes, $scope.new_comment.seconds),
                            duration: 0,
                        }],
                    };

                    $http.post(posturl, JSON.stringify(comment_json))
                        .then(function (response) {
                            var comment = {};
                            comment.timeLapse = {
                                start: response.data.selfVideoReferences[0].timestamp,
                                end: response.data.selfVideoReferences[0].timestamp
                            };
                            comment.params = {
                                comment: response.data,
                                isPopoverVisible: false,
                                cuepointVisible: 
                                        (response.data.question && $scope.checkboxModel.showquestions) ||
                                        (!response.data.question && $scope.checkboxModel.showannotations),
                            };
                            comment.onEnter = $scope.onEnter;
                            comment.onComplete = $scope.onComplete;

                            //push timestamp to timepoints on scrub bar
                            //(copy newlist variable to avoid aforementioned bug in the videogular framework)
                            
                            var newlist = angular.copy($scope.timepoints);
                            newlist.push(comment);
                            $scope.timepoints = newlist;
                            $scope.vidctrl.config.cuePoints.console = $scope.timepoints;
                
                            //push content to local comments list
                            var new_comment = response.data;
                            new_comment.videorefs = [];
                            new_comment.noterefs = [];
                            $scope.comments.push(new_comment);
                            $scope.closeCommentForm(form);
                        });
                }
            };

            $scope.closeCommentForm = function (form) {
                form.$setPristine();
                form.$setUntouched();
                $scope.new_comment = {};
                $scope.showCommentForm = true;
            };

            $scope.deleteComment = function (comm) {
                // send DELETE request to backend
                var delete_url = $scope.full_url + $scope.videoId + '/' + comm_ep + comm.commentId;
                
                $http.delete(delete_url)
                        .then(function () {
                            // deleting timestamps from scrub-bar
                            var newlist = angular.copy($scope.timepoints);
                            var cuepoint_index = [];
                            for (var i = 0; i < newlist.length; i++){
                                //delete the right cue point
                                if (newlist[i].params.comment.commentId == comm.commentId) {
                                    cuepoint_index.push(i);
                                }
                            }
                            for (var i = cuepoint_index.length-1; i >= 0; i--) {
                                newlist.splice(cuepoint_index[i],1);
                            }
                            $scope.timepoints = newlist;
                            $scope.vidctrl.config.cuePoints.console = $scope.timepoints;

                            // reloading is not necessary because the double  
                            // binding between controller and view is enough
                            var index = $scope.comments.indexOf(comm);
                            $scope.comments.splice(index, 1);
                        });
            };
            

            $scope.onEnter = function (currentTime, timeLapse, params) {
                $scope.recent.comment = params.comment;
                params.isPopoverVisible = true;
                $timeout(function () {
                    params.isPopoverVisible = false
                }, 2500);
            };
            
            $scope.onComplete = function (currentTime, timeLapse, params) {
                $scope.recent.comment = params.comment;
            };

            // sort comments
            $scope.sortOrders = ['votes','date','timestamps'];
            $scope.orderByString = '-upvotes';
    
            $scope.sort = function(){
                switch($scope.sortOrders){
                    case 'votes':
                        $scope.orderByString = '-upvotes';
                        break;
                    case 'date':
                        $scope.orderByString = '-creationTime';
                        break;
                    case 'timestamps':
                        $scope.orderByString = 'selfVideoReferences[0].timestamp';
                        break;
                }
            };
            
            $scope.updatevisible = function (){
                var newlist = angular.copy($scope.timepoints);
                for (var i = 0; i < newlist.length; i++) {
                    if (!newlist[i].params.comment.question) {
                        newlist[i].params.cuepointVisible = $scope.checkboxModel.showannotations;
                    }
                    
                    else {
                        newlist[i].params.cuepointVisible = $scope.checkboxModel.showquestions;
                    }
                }
                $scope.timepoints = newlist;
                $scope.vidctrl.config.cuePoints.console = $scope.timepoints;
            };
            
            //**********************//
            //	 EDIT FUNCTIONS	    //
            //**********************//
            $scope.editComment = function (comm) {
                var modalInstance = $uibModal.open({
                animation: true,
                scope: $scope,
                templateUrl: 'views/partials/editvideocommentform.html',
                size: 'lg',
                controller: 'EditVideoCommentController',
                resolve: {
                    comment: comm,
                    vidctrl: $scope.vidctrl,
                }
                }, 'lg');
            };
            
            //************************//
            //   VIDEO PLAYER CONFIG  //
            //************************//
            $scope.buildPlayer = function (video, comments) {
                $scope.timepoints = [];
                $scope.video = video;
                $scope.comments = comments;
                for (var i = 0; i < $scope.comments.length; i++) {
                    var selfVideoRef = $scope.comments[i].selfVideoReferences;
                    for (var j = 0; j < selfVideoRef.length; j++) {
                        var comment = {};
                        comment.timeLapse = {
                            start: selfVideoRef[j].timestamp,
                            end: selfVideoRef[j].timestamp,
                        },
                        comment.params = {
                            comment: comments[i],
                            isPopoverVisible: false,
                            cuepointVisible: true,
                        };
                        comment.onEnter = $scope.onEnter;
                        comment.onComplete = $scope.onComplete;
                        $scope.timepoints.push(comment);
                    }
                }
                $scope.getCrossrefs();
                $scope.vidctrl.media = [
                    {
                        sources: [{src: $sce.trustAsResourceUrl(video.url)}]
                    }
                ];

                $scope.vidctrl.config = {
                    playsInline: false,
                    autoHide: false,
                    autoHideTime: 3000,
                    autoPlay: false,
                    sources: $scope.vidctrl.media[0].sources,
                    loop: false,
                    preload: "auto",
                    theme: {
                        url: "http://www.videogular.com/styles/themes/default/latest/videogular.css"
                    },
                    cuePoints: {
                        console: $scope.timepoints,
                    },
                };
            };
        });

app.directive('toggle', function () {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            if (attrs.toggle == "tooltip") {
                $(element).tooltip();
            }
        }
    };
});



