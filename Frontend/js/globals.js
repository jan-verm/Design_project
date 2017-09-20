/*
 * Global variables and functions go here.
 */

// URL of the REST service
var url = 'http://student-dp8.intec.ugent.be/development/api/';
 //var url = 'http://localhost:7000/'; //for local testing

// API endpoints
var course_ep = 'course/';
var lecture_ep = 'lecture/';
var video_ep = 'video/';
var comm_ep = 'comment/';
var reply_ep = 'reply/';
var notes_ep = 'coursenotes/';
var videoref_ep = 'videoref/';
var courseref_ep = 'coursenotesref/';
var upload_ep = 'upload/';
var login_ep = 'login/';
var logout_ep = 'logout/';
var user_ep = 'user/';
var appr_ep = 'approve/';
var unappr_ep = 'unapprove/';
var upvote_ep = 'upvote/';
var downvote_ep = 'downvote/';
var subscribe_ep = 'subscribe/';
var unsubscribe_ep = 'unsubscribe/';
var ltikeypair_ep = 'lti/keypair/';


// some global helper functions

// prefixes a number < 10 with a '0'
var addZero = function (n) {
    var string = n.toString();
    while (string.length < 2) {
        string = '0' + string;
    }
    return string;
};

// convert hours, minutes and seconds to a number of seconds
var HMStoSeconds = function (hours, minutes, seconds) {
    hours = parseInt(hours);
    minutes = parseInt(minutes);
    seconds = parseInt(seconds);
    return ((hours * 60 * 60) + (minutes * 60) + seconds);
};

// converts a number of seconds to hours, minutes and seconds
var secondsToHMS = function (seconds) {
    var hours = Math.floor(seconds / 3600);
    var rest = (seconds % 3600);
    var minutes = Math.floor(rest / 60);
    seconds = Math.floor(rest % 60);
    return [hours, minutes, seconds];
};

// get the youtube video id from the url (used to get a thumbnail)
var getYoutubeId = function (url) {
    var split1 = url.split('v=');
    var split2 = split1[1].split('&');
    return split2[0];
};

// get seconds of an ISO8601 duration 
// ISO8601 respresentation: PTxHyMzS = x hours, y minutes, z seconds
var toSeconds = function (duration) {
    var h = duration.search('H');
    var m = duration.search('M');
    var s = duration.search('S');

    var seconds;
    
    if (m === -1) {
        seconds = duration.substring(2, s);
    }
    else if (h === -1) {
        seconds = 60 * duration.substring(2, m) + 1 * duration.substring(m + 1, s);

    } 
    else {
        seconds = 3600 * duration.substring(2, h) + 60 * duration.substring(h + 1, m) + 1 * duration.substring(m + 1, s);
    }
    return seconds;
};

// get a comment from a list of comments. Return the comment if found in the list, if not found, return null.
var getComment = function (commentId, commentList) {
    for (var i = 0; i < commentList.length; i++) {
        if (commentId.localeCompare(commentList[i].commentId) === 0) {
            return commentList[i];
        }
    }
    return null;
};
