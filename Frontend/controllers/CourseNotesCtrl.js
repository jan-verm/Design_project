'use strict';
var app = angular.module('app');

// get name of coursenotes for breadcrumb nav
app.service('courseNotesService', function ($cookies) {
    this.getCourseNoteName = function (id) {
        var notes = $cookies.getObject('notes');
        for (var i = 0; i < notes.length; i++) {
            if (notes[i].courseNotesId == id) {
                return notes[i].name;
            }
        }
    };
});

app.controller('CourseNotesController',
        function ($scope, $rootScope, $state, $http, $q, $stateParams, $anchorScroll, $controller, $uibModal) {
            $.extend(this, $controller('CommentController', {$scope: $scope}));
            $.extend(this, $controller('ViewerController', {$scope: $scope}));

            $scope.courseId = $stateParams.courseId;
            $rootScope.courseId = $stateParams.courseId;

            if ($stateParams.lectureId) {
                $scope.lectureId = $stateParams.lectureId;
                $scope.type = "lecture";
            } else {
                $scope.lectureId = '0';
            }
            $scope.notesId = $stateParams.notesId;

            if ($rootScope.user.sub.indexOf($scope.courseId) == -1) {
                $state.go('stateNotSubscribed', {courseId: $scope.courseId, linkstate: $stateParams});
            }
            ;

            $scope.short_url = url + course_ep + $scope.courseId + '/' + lecture_ep + $scope.lectureId + '/';
            $scope.full_url = $scope.short_url + notes_ep + $scope.notesId;
            $scope.course_material_url = $scope.full_url;
            $scope.controller = this;
            $scope.linkto = 'views/partials/linkto_notes.html';
            $scope.editform = 'views/partials/editnotescommentform.html';
            $scope.confirmdelete = "Are you sure you want to delete this comment? Deleting the comment will also delete all replies and cross-refrences."

            var classicColor = "#ff6d00";
            var questionColor = "pink";
            var crossrefColor = "blue";

            var annotationType = "annotation";
            var questionType = "question";
            var crossrefType = "crossref";

            // http get requests to build the page
            var anns_req = $http.get($scope.full_url + '/' + comm_ep);
            var courseNotes_req = $http.get($scope.full_url);
            var course_req = $http.get(url + course_ep + $scope.courseId);

            // defer requests; meaning the variables will be filled when the GET request is done
            $q.all([anns_req, courseNotes_req, $scope.iLoaded, course_req]).then(function (values) {
                $scope.comments = values[0].data;
                $scope.notes = values[1].data;
                var crossrefRequestList = $scope.getCrossrefs();

                $q.all(crossrefRequestList).then(function () {
                    $scope.reformatHighlights();
                    // Load the PDF file and the highlights.
                    // EXECUTE INIT AFTER ALL HAS COMPLETED;
                    window.initViewer($scope.notes, "classicViewer", $scope.allHighlights, $stateParams.com);
                })

                $scope.isOwner = ($rootScope.user.role == 'ADMIN' || values[3].data.owner == $rootScope.user.username);
            });

            $scope.editName = false;
            $scope.changeName = function (note) {
                var patch_url = $scope.short_url + notes_ep + $scope.notesId;
                $http.patch(patch_url, note);
            };


            // Process the data from the server into a usefull structure for the highlighting functionalities.
            // Add the color variable depending on the type of annotation.
            $scope.reformatHighlights = function () {
                $scope.allHighlights = [];
                
                for (var i = 0; i < $scope.comments.length; i++) {
                    // Choose correct color for annotation.
                    var color = classicColor;
                    var type = annotationType;
                    if ($scope.comments[i].question) {
                        color = questionColor;
                        type = questionType;
                    }

                    // Prepare the self-references for visualisation in the viewer.
                    var refs = $scope.comments[i].selfCourseNotesReferences[0].locations;
                    for (var j = 0; j < refs.length; j++) {
                        $scope.allHighlights.push({pageIndex: parseInt(refs[j].pageNumber) - 1 + "",
                            coords: [refs[j].x1,
                                refs[j].y1,
                                refs[j].x2,
                                refs[j].y2
                            ],
                            color: color,
                            visible: true,
                            type: type,
                            id: $scope.comments[i].commentId});
                    }

                    // Prepare the cross-references for visualisation in the viewer.
                    var noterefs = $scope.comments[i].noterefs;
                    if (noterefs !== undefined) {
                        for (var j = 0; j < noterefs.length; j++) {
                            // Only process the crossreferences to self.
                            if (noterefs[j].courseNotesId == $scope.notesId) {
                                var notereflocs = noterefs[j].locations;
                                for (var k = 0; k < notereflocs.length; k++) {
                                    $scope.allHighlights.push({pageIndex: parseInt(notereflocs[k].pageNumber) - 1 + "",
                                        coords: [notereflocs[k].x1,
                                            notereflocs[k].y1,
                                            notereflocs[k].x2,
                                            notereflocs[k].y2
                                        ],
                                        color: crossrefColor,
                                        visible: true,
                                        type: crossrefType,
                                        id: $scope.comments[i].commentId});
                                }
                            }
                        }
                    }

                    // Prepare the cross-references in the replies.
                    var replies = $scope.comments[i].replies;
                    if (replies !== undefined){
                        for (var l = 0; l < replies.length; l++) {
                            var replies_noterefs = replies[l].noterefs;
                            if (replies_noterefs !== undefined) {
                                for (var j = 0; j < replies_noterefs.length; j++) {
                                    // Only process the crossreferences to self.
                                    if (replies_noterefs[j].courseNotesId == $scope.notesId) {
                                        var notereflocs = replies_noterefs[j].locations;
                                        for (var k = 0; k < notereflocs.length; k++) {
                                            $scope.allHighlights.push({pageIndex: parseInt(notereflocs[k].pageNumber) - 1 + "",
                                                coords: [notereflocs[k].x1,
                                                    notereflocs[k].y1,
                                                    notereflocs[k].x2,
                                                    notereflocs[k].y2
                                                ],
                                                color: crossrefColor,
                                                visible: true,
                                                type: crossrefType,
                                                id: replies[l].replyId});
                                        }
                                    }
                                }
                            }

                            // Prepare the cross-references in the replies on the replies.
                            var replies2 = replies[l].replies;
                            if (replies2 !== undefined){
                                for (var r = 0; r < replies2.length; r++) {
                                    var replies2_noterefs = replies2[r].noterefs;
                                    if (replies2_noterefs !== undefined) {
                                        for (var j = 0; j < replies2_noterefs.length; j++) {
                                            // Only process the crossreferences to self.
                                            if (replies2_noterefs[j].courseNotesId == $scope.notesId) {
                                                var notereflocs = replies2_noterefs[j].locations;
                                                for (var k = 0; k < notereflocs.length; k++) {
                                                    $scope.allHighlights.push({pageIndex: parseInt(notereflocs[k].pageNumber) - 1 + "",
                                                        coords: [notereflocs[k].x1,
                                                            notereflocs[k].y1,
                                                            notereflocs[k].x2,
                                                            notereflocs[k].y2
                                                        ],
                                                        color: crossrefColor,
                                                        visible: true,
                                                        type: crossrefType,
                                                        id: replies2[r].replyId});
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                ;
            };

            //**********************
            // ANNOTATION FUNCTIONS
            //**********************
            // Keeps track of all highlighted boxes.
            $scope.allHighlights = [];
            $scope.highlightToAdd = null;

            $scope.showEditCommentForm = true;

            // This function is called to signal that everything for the viewer is ready to be used.
            window.iframeLoaded = function () {
                $scope.iLoaded = new Promise(function (resolve, reject) {
                    resolve('iFrame loaded');
                });
            };


            $scope.openCommentForm = function () {
                $scope.showCommentForm = false;
                $scope.selectText(false);
            };

            $scope.closeCommentForm = function (form, type) {
                switch (type) {
                    case "addComment":
                        $scope.new_comment = {};
                        $scope.showCommentForm = true;
                        break;
                    case "editComment":
                        $scope.showEditCommentForm = true;
                        // Change the class so css can change the look for some time.
                        document.getElementById($scope.comments[$scope.edited_comment.index].commentId).setAttribute("class", "panel-body comment");
                }

                if ($scope.highlightToAdd !== null) {
                    // Redraw the highlights in the viewer to erase the would-be-highlighted one.
                    document.getElementById("classicViewer").contentWindow.highlightUpdateAll($scope.allHighlights);
                    $scope.highlightToAdd = null;
                }
                form.$setPristine();
                form.$setUntouched();
            };

            // Set the scope of the commentform, so it is possible to refer to the form.
            $scope.setFormScope = function (scope) {
                $scope.commentFormScope = scope;
            };

            // Does the action to highlight the selected text. If nothing was selected, give an error message if the alert boolean is true.
            $scope.selectText = function (alert) {
                // Clear previous highlight from memory.
                $scope.highlightToAdd = null;
                // Draw the new highlight in the viewer green. The function will return the coordinates and pages.
                $scope.highlightToAdd = document.getElementById("classicViewer").contentWindow.highlightAll($scope.allHighlights, 'green');
                // If the highlight is empty, is invalid.
                if ($scope.highlightToAdd.length === 0 || $scope.highlightToAdd[0].coords[0] === $scope.highlightToAdd[0].coords[2]) {
                    if (alert) {
                        window.alert("Please select some text in the viewer.");
                    }
                    $scope.highlightToAdd = null;
                } else {
                    // Deselect the text selected by the user.
                    document.getElementById("classicViewer").contentWindow.getSelection().removeAllRanges();
                }
            };

            // Check if the new highlightToAdd is overlapping with the current highlights.
            $scope.isHighlightToAddOverlapping = function () {
                // Skip if nothing is selected
                if ($scope.highlightToAdd === null) {
                    return false;
                }
                for (var i = 0; i < $scope.allHighlights.length; i++) {
                    for (var j = 0; j < $scope.highlightToAdd.length; j++) {
                        // Skip if the highlights are on different pages.
                        if ($scope.highlightToAdd[j].pageIndex === $scope.allHighlights[i].pageIndex) {
                            if (!(Math.min($scope.highlightToAdd[j].coords[0], $scope.highlightToAdd[j].coords[2]) > Math.max($scope.allHighlights[i].coords[0], $scope.allHighlights[i].coords[2]) ||
                                    Math.max($scope.highlightToAdd[j].coords[0], $scope.highlightToAdd[j].coords[2]) < Math.min($scope.allHighlights[i].coords[0], $scope.allHighlights[i].coords[2]) ||
                                    Math.min($scope.highlightToAdd[j].coords[1], $scope.highlightToAdd[j].coords[3]) > Math.max($scope.allHighlights[i].coords[1], $scope.allHighlights[i].coords[3]) ||
                                    Math.max($scope.highlightToAdd[j].coords[1], $scope.highlightToAdd[j].coords[3]) < Math.min($scope.allHighlights[i].coords[1], $scope.allHighlights[i].coords[3]))) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            };

            $scope.convertLocations = function (locations) {
                var convertedLocations = [];
                for (var i = 0; i < locations.length; i++) {
                    convertedLocations.push({pageNumber: parseInt(locations[i].pageIndex) + 1,
                        x1: locations[i].coords[0],
                        y1: locations[i].coords[1],
                        x2: locations[i].coords[2],
                        y2: locations[i].coords[3]
                    });
                }
                return convertedLocations;
            };

            $scope.submitComment = function (form) {
                // Stop if there is no text selected.
                if ($scope.highlightToAdd === null) {
                    return;
                }
                var confirmed = true;
                if ($scope.isHighlightToAddOverlapping()) {
                    confirmed = confirm("Are you sure you want to add an overlapping highlight?");
                }

                if (confirmed && form.$valid) {
                    if (!$scope.new_comment.question) {
                        $scope.new_comment.question = false;
                    }
                    var posturl = $scope.short_url + notes_ep + $scope.notesId + '/' + comm_ep;
                    var locations = $scope.convertLocations($scope.highlightToAdd);

                    // build the JSON to be posted
                    var comment_json = {
                        username: $rootScope.user.username,
                        body: $scope.new_comment.text,
                        question: $scope.new_comment.question,
                        approved: false,
                        selfCourseNotesReferences: [{
                                courseNotesId: $scope.notesId,
                                locations: locations
                            }]
                    };
                    $http.post(posturl, JSON.stringify(comment_json))
                            .then(function (response) {
                                // Add annotation to viewer.
                                var new_comment = response.data;
                                new_comment.videorefs = [];
                                new_comment.noterefs = [];
                                $scope.comments.push(new_comment);
                                // Recalculate the $scope.allHighlights array for drawing the hightlights in the viewer.
                                $scope.reformatHighlights();
                                // Reset the highlightToAdd variable.
                                $scope.highlightToAdd = null;
                                $scope.closeCommentForm(form, "addComment");

                                // Redraw the highlights in the viewer.
                                document.getElementById("classicViewer").contentWindow.highlightUpdateAll($scope.allHighlights);
                            });
                }
            };

            $scope.deleteComment = function (comm) {
                // Build URL to send DELETE request to.
                var delete_url = $scope.full_url + '/' + comm_ep + comm.commentId;

                // send DELETE request to backend
                $http.delete(delete_url)
                        .success(function () {
                            // Delete annotation from viewer.
                            var index = $scope.comments.indexOf(comm);
                            $scope.comments.splice(index, 1);
                            // Recalculate the allHighlights array for drawing the hightlights in the viewer.
                            $scope.reformatHighlights();
                            // Redraw the highlights in the viewer.
                            document.getElementById("classicViewer").contentWindow.highlightUpdateAll($scope.allHighlights);
                        });
            };

            $scope.scrollTo = function (pageNumber, coordX, coordY, commentId) {
                $anchorScroll();
                document.getElementById("classicViewer").contentWindow.scroll(pageNumber, coordX, coordY + 10, commentId);
            };

            // sort comments
            $scope.sortOrders = ['votes', 'date', 'page number'];
            $scope.orderByString = '-upvotes';

            $scope.sort = function () {
                switch ($scope.sortOrders) {
                    case 'votes':
                        $scope.orderByString = '-upvotes';
                        break;
                    case 'date':
                        $scope.orderByString = '-creationTime';
                        break;
                    case 'page number':
                        $scope.orderByString = 'selfCourseNotesReferences[0].locations[0].pageNumber';
                        break;
                }
            };

            //**********************//
            //	 EDIT FUNCTIONS	    //
            //**********************//
            $scope.edited_comment = {};

            $scope.editComment = function (comm) {
                var index = $scope.comments.indexOf(comm);
                
                // If the add comment form is open, close it.
                $scope.closeCommentForm($scope.commentFormScope.commentform, "addComment");

                $scope.showEditCommentForm = false;
                $scope.highlightToAdd = $scope.comments[index].selfCourseNotesReferences[0].locations;
                $scope.edited_comment.question = $scope.comments[index].question;
                $scope.edited_comment.body = $scope.comments[index].body;
                $scope.edited_comment.index = index;

                // Scroll to the comment and show an indicator on the comment while it is in edit mode.
                scrollToComment($scope.comments[index].commentId, 1);
            };

            $scope.save = function (form) {
                // Stop if there is no text selected.
                if ($scope.highlightToAdd === null) {
                    return;
                }
                // change comment locally
                var index = $scope.edited_comment.index;

                var confirmed = true;
                if ($scope.isHighlightToAddOverlapping()) {
                    confirmed = confirm("Are you sure you want to add an overlapping highlight?");
                }

                if (confirmed && form.$valid) {
                    $scope.comments[index].body = $scope.edited_comment.body;
                    $scope.comments[index].question = $scope.edited_comment.question;
                    // Check if highlight has changed.
                    if ($scope.comments[index].selfCourseNotesReferences[0].locations !== $scope.highlightToAdd) {
                        var locations = $scope.convertLocations($scope.highlightToAdd);
                        $scope.comments[index].selfCourseNotesReferences[0].locations = locations;
                    }

                    // send PATCH request to backend
                    var patch_url = $scope.full_url + $scope.notesId + '/' + comm_ep + $scope.comments[index].commentId;
                    $http.patch(patch_url, JSON.stringify($scope.comments[index]))
                            .then(function () {
                                $scope.closeCommentForm(form, "editComment");
                                // Recalculate the $scope.allHighlights array for drawing the hightlights in the viewer.
                                $scope.reformatHighlights();
                                // Reset the highlightToAdd variable.
                                $scope.highlightToAdd = null;
                                // Redraw the highlights in the viewer.
                                document.getElementById("classicViewer").contentWindow.highlightUpdateAll($scope.allHighlights);
                            });
                }
            };

            // Open the download dialog in a new modal.
            $scope.downloadPDF = function () {
                var modalInstance = $uibModal.open({
                    animation: true,
                    scope: $scope,
                    templateUrl: 'views/partials/downloadform.html',
                    controller: 'DownloadPdfController',
                    size: 'dialog',
                    resolve: {
                    }
                }, 'lg');
            };

            $scope.reloadVisualizations = function () {
                for (var j = 0; j < $scope.allHighlights.length; j++) {
                    if ($scope.allHighlights[j].type == annotationType) {
                        $scope.allHighlights[j].visible = $scope.checkboxModel.showannotations;
                    } else if ($scope.allHighlights[j].type == questionType) {
                        $scope.allHighlights[j].visible = $scope.checkboxModel.showquestions;
                    }
                }
                document.getElementById("classicViewer").contentWindow.highlightUpdateAll($scope.allHighlights);
            };

        });

// Scroll to a comment with the given id.
function scrollToComment(id, permanent) {
    // Scroll for long mode.
    document.getElementById(id).scrollIntoView();
    // 
    window.scrollBy(0, -$(".navbar").outerHeight(true));

    // Scroll for wide mode.
    // Compute the correct offset.
    var offset = document.getElementById('coursenotescommentsection').getBoundingClientRect().top - document.getElementById(id).getBoundingClientRect().top;
    // Do not scroll if the comment is already visible.
    if (offset !== 0) {
        document.getElementById('coursenotescommentsection').scrollTop = document.getElementById('coursenotescommentsection').scrollTop - offset;
    }
    // Change the class so css can change the look for some time.
    document.getElementById(id).setAttribute("class", "panel-body comment scrolledintoview");
    if (!permanent) {
        setTimeout(function () {
            document.getElementById(id).setAttribute("class", "panel-body comment");
        }, 3000);
    }
}