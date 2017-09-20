'use strict';
var app = angular.module('app');

//****************
// Controller takes care of the initialization of the viewer. 
//****************

app.controller('ViewerController', function ($scope, $timeout) {

    window.initViewer = function (notes, viewerName, allHighlights, commentToScrollId) {
        // Load PDF into viewer.
        if (notes !== undefined && document.getElementById(viewerName).contentWindow.PDFViewerApplication !== undefined) {
            document.getElementById(viewerName).contentWindow.PDFViewerApplication.open(notes.url)
                    .then(
                            // On success.
                                    function () {
                                        // If the com parameter is set, scroll to the comment with commentId equal to com.
                                        if (commentToScrollId !== undefined) {
                                            var linkedComment = getComment(commentToScrollId, $scope.comments);
                                            if (linkedComment !== null) {
                                                scrollToComment(linkedComment.commentId, 0);
                                                $scope.scrollTo(linkedComment.selfCourseNotesReferences[0].locations[0].pageNumber, null, linkedComment.selfCourseNotesReferences[0].locations[0].y1, linkedComment.commentId);
                                            }
                                        }
                                    },
                                    // When the loading failed, try again.
                                            function () {
                                                console.warn("Failed to load pdf in this attempt. Don't panic I will try until it works. [open]");
                                                $timeout(function () {
                                                    window.initViewer(notes, "classicViewer", allHighlights, commentToScrollId);
                                                }, 1000);
                                            }
                                    );

                                    // Redraw the highlights in the viewer.
                                    document.getElementById(viewerName).contentWindow.highlightUpdateAll(allHighlights);
                                } else {
                            console.warn("Failed to load pdf in this attempt. Don't panic I will try until it works. [not ready]");
                            
                            $timeout(function () {
                                window.initViewer(notes, "classicViewer", allHighlights, commentToScrollId);
                            }, 1000);
                        }
                    };
        });