'use strict';
var app = angular.module('app');

app.controller('EditReplyController',
        function ($scope, $http, $uibModalInstance, parent, reply) {
            var parent_index = $scope.comments.indexOf(parent);
            var reply_index = parent.replies.indexOf(reply);

            $scope.edited_reply = reply;

            $scope.cancelEditReply = function () {
                $scope.edited_reply = {};
                $uibModalInstance.close();
            };

            $scope.saveEditReply = function () {
                parent.replies[reply_index] = $scope.edited_reply;

                // send PATCH request to backend
                var patch_url = $scope.short_url + reply_ep + $scope.edited_reply.replyId;
                $http.patch(patch_url, JSON.stringify($scope.edited_reply));

                $uibModalInstance.close();
            };
        });
