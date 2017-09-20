// inject dependencies in one place. (here.)
var app = angular.module('app', [
    "ui.router",
    "ui.validate",
    "ui.bootstrap",
    "ngSanitize",
    "com.2fdevs.videogular",
    "com.2fdevs.videogular.plugins.controls",
    "com.2fdevs.videogular.plugins.overlayplay",
    "info.vietnamcode.nampnq.videogular.plugins.youtube",
    "angularUtils.directives.uiBreadcrumbs",
    "ngCookies",
    "angular-cookie-law",
]);

app.run(function ($rootScope, $cookies, $state, $http) {
    $rootScope.$on('$locationChangeSuccess', function (e) {
        // check if a user is logged in
        var user = $cookies.getObject("user");
        if (user) {
            $rootScope.user = user;
        } else {
            // if not, send them to the login page, if they are not on the signup or the homepage
            if ($state.current.name && $state.current.name != 'stateSignUp' && $state.current.name != 'stateHome') {
                $state.go('stateLogin');
            }
        }
        ;
    });
});

// ui-router route configuration
app.config(function ($stateProvider, $urlRouterProvider, $httpProvider, $provide) {
    $urlRouterProvider.when('', '/');
    // credentials make sure the session key is stored in browser
    $httpProvider.defaults.withCredentials = true;
    $httpProvider.defaults.headers = {
        'Content-Type': 'application/json; charset=utf-8'
    };

    $stateProvider

            // index state
            .state('stateHome', {
                url: '/',
                data: {
                    displayName: 'Classic'
                },
                views: {
                    '@': {
                        templateUrl: 'views/home.html',
                        controller: 'HomeController',
                        controllerAs: 'homectrl',
                    }
                },
            })

            // login state
            .state('stateLogin', {
                url: 'login',
                parent: 'stateHome',
                data: {
                    displayName: 'Login'
                },
                views: {
                    '@': {
                        templateUrl: 'views/login.html',
                        controller: 'LoginController',
                    }
                }
            })

            // signup state
            .state('stateSignUp', {
                url: 'signup',
                parent: 'stateHome',
                data: {
                    displayName: 'Sign Up'
                },
                views: {
                    '@': {
                        templateUrl: 'views/signup.html',
                        controller: 'SignUpController',
                    }
                }
            })

            // account state
            .state('stateAccount', {
                url: 'account',
                parent: 'stateHome',
                data: {
                    displayName: 'My Account'
                },
                views: {
                    '@': {
                        templateUrl: 'views/account.html',
                        controller: 'AccountController',
                        controllerAs: 'accountctrl',
                    }
                }
            })

            // subscribed courses state
            .state('stateCourses', {
                url: 'mycourses',
                parent: 'stateHome',
                data: {
                    displayName: 'My Courses'
                },
                views: {
                    '@': {
                        templateUrl: 'views/mycourses.html',
                        controller: 'MyCoursesController',
                        controllerAs: 'mycoursesctrl',
                    }
                },
            })

            // browse courses state
            .state('stateBrowseCourses', {
                url: '/browse',
                parent: 'stateCourses',
                data: {
                    displayName: 'Browse All Courses'
                },
                views: {
                    '@': {
                        templateUrl: 'views/browsecourses.html',
                        controller: 'BrowseCoursesController',
                        controllerAs: 'browsectrl',
                    }
                },
            })

            // course detail page
            .state('stateCourse', {
                url: '/course/:courseId',
                parent: 'stateCourses',
                data: {
                    displayName: '{{courseName}}'
                },
                views: {
                    '@': {
                        templateUrl: 'views/course.html',
                        controller: 'CourseController',
                        controllerAs: 'coursectrl',
                    }
                },
                resolve: {
                    courseName: function (courseService, $stateParams) {
                        return courseService.getCourseName($stateParams.courseId);
                    }
                },
            })

            // Video player page for coursevideos (no lecture)
            .state('stateCourseVideo', {
                url: '/lecture/0/video/:videoId?:t',
                parent: 'stateCourse',
                data: {
                    displayName: '{{videoName}}'
                },
                views: {
                    '@': {
                        templateUrl: 'views/videoplayer.html',
                        controller: 'VideoController',
                        controllerAs: 'vidctrl',
                    }
                },
                resolve: {
                    videoName: function (videoService, $stateParams) {
                        return videoService.getVideoName($stateParams.videoId);
                    }
                },
                params: {
                    t: '0',
                }
            })

            // Course notes page for course notes (no lecture)
            .state('stateCourseCourseNotes', {
                url: '/lecture/0/coursenotes/:notesId?:com',
                parent: 'stateCourse',
                data: {
                    displayName: '{{courseName}}'
                },
                views: {
                    '@': {
                        templateUrl: 'views/coursenotes.html',
                        controller: 'CourseNotesController',
                        controllerAs: 'coursenotesctrl',
                    }
                },
                resolve: {
                    courseName: function (courseNotesService, $stateParams) {
                        return courseNotesService.getCourseNoteName($stateParams.notesId);
                    }
                }
            })

            // lecture detail page
            .state('stateLecture', {
                url: '/lecture/:lectureId',
                parent: 'stateCourse',
                data: {
                    displayName: '{{lectureName}}'
                },
                views: {
                    '@': {
                        templateUrl: 'views/course.html',
                        controller: 'CourseController',
                        controllerAs: 'coursectrl',
                    }
                },
                resolve: {
                    lectureName: function (lectureService, $stateParams) {
                        return lectureService.getLectureName($stateParams.lectureId);
                    }
                },
            })

            // LTI state
            .state('stateLTI', {
                url: '/lti?:role:sessionId:username:id',
                data: {
                    displayName: 'Classic'
                },
                views: {
                    '@': {
                        controller: 'LTIController',
                        controllerAs: 'ltictrl',
                    }
                },
            })

            // Video player page
            .state('stateVideo', {
                url: '/video/:videoId?:t',
                parent: 'stateLecture',
                data: {
                    displayName: '{{videoName}}'
                },
                views: {
                    '@': {
                        templateUrl: 'views/videoplayer.html',
                        controller: 'VideoController',
                        controllerAs: 'vidctrl',
                    }
                },
                resolve: {
                    videoName: function (videoService, $stateParams) {
                        return videoService.getVideoName($stateParams.videoId);
                    }
                },
                params: {
                    t: '0',
                }
            })

            // Course notes page
            .state('stateCourseNotes', {
                url: '/coursenotes/:notesId?:com',
                parent: 'stateLecture',
                data: {
                    displayName: '{{courseName}}'
                },
                views: {
                    '@': {
                        templateUrl: 'views/coursenotes.html',
                        controller: 'CourseNotesController',
                        controllerAs: 'coursenotesctrl',
                    }
                },
                resolve: {
                    courseName: function (courseNotesService, $stateParams) {
                        return courseNotesService.getCourseNoteName($stateParams.notesId);
                    }
                }
            })

            // Unauthorized error
            .state('stateError401', {
                url: 'err401/:errmsg',
                parent: 'stateHome',
                data: {
                    displayName: 'Unauthorized'
                },
                views: {
                    '@': {
                        templateUrl: 'views/err401.html',
                        controller: 'UnauthController',
                    }
                },
            })

            // Internal Server error
            .state('stateError500', {
                url: 'err500/:errmsg',
                parent: 'stateHome',
                data: {
                    displayName: 'Error'
                },
                views: {
                    '@': {
                        templateUrl: 'views/err500.html',
                        controller: 'InternalErrorController',
                    }
                },
            })

            // state for when user is not subscribed to a course
            .state('stateNotSubscribed', {
                url: 'nosub/:courseId',
                parent: 'stateHome',
                data: {
                    displayName: 'Not Subscribed'
                },
                views: {
                    '@': {
                        templateUrl: 'views/nosub.html',
                        controller: 'NotSubscribedController',
                    }
                },
                params: {
                    // invisible state params; contains the state params of the last state 
                    // (to be able to send the user to the correct state when they subscribe)
                    linkstate: {squash: true, value: null}
                }
            });

    // request interceptor
    $provide.factory('httpRequestInterceptor', function ($q, $location) {
        return {
            'responseError': function (rejection) {
                switch (rejection.status) {
                    //to be continued 
                    case 401: // authorization error
                        if (rejection.data.indexOf('voted') == -1) {
                            $location.path('/err401/' + rejection.data);
                        }
                        return $q.reject(rejection);
                    case 500:
                        $location.path('/err500/' + rejection.data)
                    default:
                        return $q.reject(rejection);
                }
            }
        };
    });
    $httpProvider.interceptors.push('httpRequestInterceptor');
});

// global filter
app.filter('secondsToDateTime', [function () {
        return function (seconds) {
            return new Date(1970, 0, 1).setSeconds(seconds);
        };
    }]);

// global directive (provides 'Are you sure?' popups on delete)
app.directive('ngReallyClick', [function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                element.bind('click', function () {
                    var message = attrs.ngReallyMessage;
                    if (message && confirm(message)) {
                        scope.$apply(attrs.ngReallyClick);
                    }
                });
            }
        };
    }]);
