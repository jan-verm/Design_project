'use strict';
var app = angular.module('app');

app.controller('DownloadPdfController',
        function ($scope, $uibModalInstance) {

            // Initialize download options.
            $scope.checkboxDownloadModel = {
                addannotations: true,
                addquestions: true
            };

            $scope.cancelDownload = function () {
                $uibModalInstance.close();
            };

            $scope.download = function () {
                if ($scope.checkboxDownloadModel.addannotations && $scope.checkboxDownloadModel.addquestions) {
                    window.open($scope.full_url + '/download-all', '_blank');
                } else if ($scope.checkboxDownloadModel.addannotations) {
                    window.open($scope.full_url + '/download-annotations', '_blank');
                } else if ($scope.checkboxDownloadModel.addquestions) {
                    window.open($scope.full_url + '/download-questions', '_blank');
                } else {
                    window.open($scope.full_url + '/download-none', '_blank');
                }
                $uibModalInstance.close();
            };
        });
