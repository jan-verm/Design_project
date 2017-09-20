'use strict';
var app = angular.module('app');

app.controller('EditVideoCommentController',
        function ($scope, $http, $uibModalInstance, comment, vidctrl) {
            var index = $scope.comments.indexOf(comment);

            $scope.edited_comment = {};
            $scope.edited_comment.body = comment.body;
            $scope.edited_comment.question = comment.question;

            // get and format timestamp
            var date = new Date(0, 0, 1);
            date.setSeconds($scope.comments[index].selfVideoReferences[0].timestamp);
            $scope.edited_comment.hours = addZero(date.getHours());
            $scope.edited_comment.minutes = addZero(date.getMinutes());
            $scope.edited_comment.seconds = addZero(date.getSeconds());

            // cancel the edit
            $scope.cancel = function () {
                $scope.edited_comment = {};
                $uibModalInstance.close();
            };

            // edit comment
            $scope.save = function () {
                var seconds = HMStoSeconds(
                        $scope.edited_comment.hours,
                        $scope.edited_comment.minutes,
                        $scope.edited_comment.seconds
                );

                $scope.comments[index].body = $scope.edited_comment.body;
                $scope.comments[index].question = $scope.edited_comment.question;

                // edit the cuepoint list
                // (copy because of a bug in the videogular framework)
                var newlist = angular.copy($scope.timepoints);
                var timepoint = {
                    timeLapse: {
                        start: seconds,
                        end: seconds,
                    },
                    params: {
                        comment: $scope.comments[index],
                        isPopoverVisible: false,
                        cuepointVisible: true,
                    },
                    onEnter: $scope.onEnter,
                    onComplete: $scope.onComplete,
                };

                newlist.splice(index, 1, timepoint);
                $scope.timepoints = newlist;
                vidctrl.config.cuePoints.console = $scope.timepoints;

                // send PATCH request to backend
                var patch_url = $scope.full_url + $scope.videoId + '/' + comm_ep + $scope.comments[index].commentId;
                $http.patch(patch_url, JSON.stringify($scope.comments[index]));

                // if timestamp changed, also send PATCH to ref
                if ($scope.comments[index].selfVideoReferences[0].timestamp != seconds) {
                    $scope.comments[index].selfVideoReferences[0].timestamp = seconds;
                    var id = $scope.comments[index].selfVideoReferences[0].refId;
                    var patch_url = $scope.short_url + comm_ep + $scope.comments[index].commentId + '/' + videoref_ep + id + '/';
                    $http.patch(patch_url, JSON.stringify($scope.comments[index].selfVideoReferences[0]));
                };

                $uibModalInstance.close();
            };
        });
