'use strict';
var app = angular.module('app');

// this controller is injected in both Video and CourseNotes pages
app.controller('CommentController', function ($scope, $rootScope, $uibModal, $http) {

    //**********************
    // ANNOTATION FUNCTIONS
    //**********************

    // Initialize empty comment.       
    $scope.showCommentForm = true;
    $scope.new_comment = {};

    // Initialize comment filter.
    $scope.checkboxModel = {
        showannotations: true,
        showquestions: true
    };

    $scope.sendPatchToComment = function (comm) {
        var patch_url = $scope.course_material_url + '/' + comm_ep + comm.commentId;
        $http.patch(patch_url, JSON.stringify(comm));
    };

    $scope.approveComment = function (comm) {
        var post_url = url + comm_ep + comm.commentId + '/' + appr_ep;
        comm.approved = true;
        $http.post(post_url);
    };

    $scope.unapproveComment = function (comm) {
        var post_url = url + comm_ep + comm.commentId + '/' + unappr_ep;
        comm.approved = false;
        $http.post(post_url);
    };

    $scope.upvoteComment = function (comm) {
        var post_url = url + comm_ep + comm.commentId + '/' + upvote_ep;
        $http.post(post_url).then (function () {
            comm.upvotes++;
            comm.alrvoted = false;
        })
        .catch( function () {
            comm.alrvoted = true;
        });
    };

    $scope.downvoteComment = function (comm) {
        var post_url = url + comm_ep + comm.commentId + '/' + downvote_ep;
        $http.post(post_url).then (function () {
            comm.upvotes--;
            comm.alrvoted = false;
        })
        .catch( function () {
            comm.alrvoted = true;
        });
    };

    //*****************
    // REPLY FUNCTIONS
    //*****************

    // Initialize empty reply.
    $scope.new_reply = {};
    $scope.showAnswerForm = false;

    // Add a new reply to the comment comm.
    $scope.submitReply = function (comm) {
        var post_url = $scope.short_url + comm_ep + comm.commentId + '/children';
        $scope.sendReply(comm, post_url);
    };

    $scope.sendReply = function (comm, url) {
        var reply_json = {
            user: $rootScope.user.username,
            body: $scope.new_reply.body,
        };
        $http.post(url, JSON.stringify(reply_json))
                .then(function (response) {
                    var data = response.data;
                    data.videorefs = [];
                    data.noterefs = [];
                    comm.replies.push(data);
                    $scope.new_reply = {};
                });
    };

    $scope.upvoteReply = function (reply) {
        var post_url = url + reply_ep + reply.replyId + '/' + upvote_ep;
        $http.post(post_url).then (function () {
            reply.upvotes++;
            reply.alrvoted = false;
        })
        .catch( function () {
            reply.alrvoted = true;
        });
    };

    $scope.downvoteReply = function (reply) {
        var post_url = url + reply_ep + reply.replyId + '/' + downvote_ep;
        $http.post(post_url).then (function () {
            reply.upvotes--;
            reply.alrvoted = false;
        })
        .catch( function () {
            reply.alrvoted = true;
        });
    };

    $scope.editReply = function (comm, reply) {
        // open edit reply modal
        var modalInstance = $uibModal.open({
            animation: true,
            scope: $scope,
            templateUrl: 'views/partials/editreplyform.html',
            size: 'lg',
            controller: 'EditReplyController',
            resolve: {
                parent: comm,
                reply: reply
            }
        }, 'lg');
    };

    $scope.deleteReply = function (parent, reply) {
        var delete_url = $scope.short_url + reply_ep + reply.replyId;

        $http.delete(delete_url).then(function () {
            var position = parent.replies.indexOf(reply);
            parent.replies.splice(position, 1)
        });

    };

    $scope.approveReply = function (reply) {
        var post_url = url + reply_ep + reply.replyId + '/' + appr_ep;
        reply.approved = true;
        $http.post(post_url);
    };

    $scope.unapproveReply = function (reply) {
        var post_url = url + reply_ep + reply.replyId + '/' + unappr_ep;
        reply.approved = false;
        $http.post(post_url);
    };

    // REPLY TO A REPLY
    $scope.showReplyForm = false;
    $scope.submitReplyToReply = function (reply) {
        var post_url = $scope.short_url + reply_ep + reply.replyId + '/children';
        $scope.sendReply(reply, post_url);
    };


    //***************************
    // CROSS-REFERENCE FUNCTIONS
    //***************************

    $scope.getCrossrefs = function () {
        var list = [];
        // get crossrefs for comments
        $scope.comments.forEach(function (comm) {
            list.push($http.get($scope.short_url + comm_ep + comm.commentId + '/' + videoref_ep).then(function (response) {
                comm.videorefs = [];
                var first = true;
                response.data.forEach(function (ref) {
                    if (ref.videoId == $scope.videoId && first) {
                        first = false;
                    } else {
                        comm.videorefs.push(ref);
                    }
                });
            }));

            list.push($http.get($scope.short_url + comm_ep + comm.commentId + '/' + courseref_ep).then(function (response) {
                comm.noterefs = [];
                var first = true;
                response.data.forEach(function (ref) {
                    if (ref.courseNotesId == $scope.notesId && first) {
                        first = false;
                    } else {
                        comm.noterefs.push(ref);
                    }
                });
            }));

            // get crossrefs  for replies
            comm.replies.forEach(function (reply) {
                list.push($http.get(url + reply_ep + reply.replyId + '/' + videoref_ep).then(function (response) {
                    reply.videorefs = [];
                    var first = true;
                    response.data.forEach(function (ref) {
                        reply.videorefs.push(ref);
                    });
                }));

                list.push($http.get(url + reply_ep + reply.replyId + '/' + courseref_ep).then(function (response) {
                    reply.noterefs = [];
                    var first = true;
                    response.data.forEach(function (ref) {
                        reply.noterefs.push(ref);
                    });
                }));

                // get crossrefs for replies on replies
                reply.replies.forEach(function (reply2) {
                    list.push($http.get(url + reply_ep + reply2.replyId + '/' + videoref_ep).then(function (response) {
                        reply2.videorefs = [];
                        var first = true;
                        response.data.forEach(function (ref) {
                            reply2.videorefs.push(ref);
                        });
                    }));

                    list.push($http.get(url + reply_ep + reply2.replyId + '/' + courseref_ep).then(function (response) {
                        reply2.noterefs = [];
                        var first = true;
                        response.data.forEach(function (ref) {
                            reply2.noterefs.push(ref);
                        });
                    }));
                });
            });
        });
        return list;
    };

    $scope.openCrossRef = function (comm, size) {
        //open cross ref modal
        var modalInstance = $uibModal.open({
            animation: true,
            scope: $scope,
            templateUrl: 'views/crossref.html',
            controller: 'CrossRefController',
            size: size,
            resolve: {
                comm_index: $scope.comments.indexOf(comm),
                reply_index: -1,
                reply2reply_index: -1,
            }
        }, 'lg');
    };


    $scope.openCrossRefReply = function (comm, reply, size) {
        var modalInstance = $uibModal.open({
            animation: true,
            scope: $scope,
            templateUrl: 'views/crossref.html',
            controller: 'CrossRefController',
            size: size,
            resolve: {
                comm_index: $scope.comments.indexOf(comm),
                reply_index: comm.replies.indexOf(reply),
                reply2reply_index: -1,
            }
        }, 'lg');
    };

    $scope.openCrossRefReply2Reply = function (comm, reply, reply2, size) {
        var modalInstance = $uibModal.open({
            animation: true,
            scope: $scope,
            templateUrl: 'views/crossref.html',
            controller: 'CrossRefController',
            size: size,
            resolve: {
                comm_index: $scope.comments.indexOf(comm),
                reply_index: comm.replies.indexOf(reply),
                reply2reply_index: reply.replies.indexOf(reply2),
            }
        }, 'lg');
    };

})