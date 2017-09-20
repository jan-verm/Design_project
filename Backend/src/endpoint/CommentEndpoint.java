package endpoint;

import interfaces.CommentControllerInterface;
import interfaces.CourseNotesControllerInterface;
import interfaces.CourseNotesReferenceControllerInterface;
import interfaces.UserControllerInterface;
import interfaces.VideoControllerInterface;
import interfaces.VideoReferenceControllerInterface;

import java.util.ArrayList;
import java.util.List;

import models.Comment;
import models.CourseNotes;
import models.CourseNotesReference;
import models.Reply;
import models.Video;
import models.VideoReference;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import parser.CommentParser;
import parser.CourseNotesReferenceParser;
import parser.VideoReferenceParser;
import security.AuthenticationChecker;
import config.AbstractConfig;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import exceptionhandler.ExceptionHandler;
import exceptionhandler.Runner;

/**
 * @author Jan Vermeulen
 */
@RestController
public class CommentEndpoint {

    private CommentControllerInterface commentController;
    private VideoControllerInterface videoController;
    private VideoReferenceControllerInterface videoReferenceController;
    private CourseNotesControllerInterface courseNotesController;
    private CourseNotesReferenceControllerInterface courseNotesReferenceController;
    private ExceptionHandler exceptionHandler;
    private AuthenticationChecker authChecker;
    private UserControllerInterface userController;
    
    /**
     * Create a new CommentEndpoint and initialize controller dependencies.
     *
     * @return CommentEndpoint
     */
    @Autowired
    public CommentEndpoint(AbstractConfig config) {
    	commentController = config.getCommentController();
    	videoController = config.getVideoController();
        videoReferenceController = config.getVideoReferenceController();
        courseNotesController = config.getCourseNotesController();
        courseNotesReferenceController = config.getCourseNotesReferenceController();
        exceptionHandler = config.getExceptionHandler();
        authChecker = config.getAuthenticationChecker();
        userController = config.getUserController();
    }
    
    
    /**
     * Add a new video comment.
     *
     * @param Id of course that contains video
     * @param Id of lecture that contains video
     * @param Id of video
     * @param Json string containing the new comment object
     * @return newly created comment
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/video/{videoId}/comment", method = RequestMethod.POST)
    public ResponseEntity<String> postVideoComment(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int videoId, @RequestBody final String body) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "postVideoComment") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String answer = "";
				// Parse comment
	    		Comment comment = CommentParser.to(body);
		    	comment.setUser(userController.getUser(authChecker.getCurrentUsername()));
		    	// Parse Self References
	    		String selfVideoRefsJSON = CommentParser.getSelfVideoRefs(body).toString();
	    		Video video = videoController.getVideo(videoId);
	    		List<VideoReference> selfVideoRefs = VideoReferenceParser.toList(selfVideoRefsJSON, video);
		    	// Add comment and self references
		    	int id = commentController.addVideoComment(videoId, comment, selfVideoRefs, authChecker.getCurrentUserId());
		    	// Get posted comment and return
		    	comment = commentController.getComment(id);
		    	answer = CommentParser.from(comment, selfVideoRefs, null).toString();
		    	return new ResponseEntity<String>(answer, HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
    
    /**
     * Add a new coursenotes comment.
     *
     * @param Id of course that contains coursenotes
     * @param Id of lecture that contains coursenotes
     * @param Id of coursenotes
     * @param Json string containing the new comment object
     * @return newly created comment
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/coursenotes/{courseNotesId}/comment", method = RequestMethod.POST)
    public ResponseEntity<String> postCourseNotesComment(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int courseNotesId, @RequestBody final String body) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "postCourseNotesComment") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String response = "";
		    	
		    	// Parse comment
		    	Comment comment = CommentParser.to(body);
		    	comment.setUser(userController.getUser(authChecker.getCurrentUsername()));

		    	// Parse Self References
		    	String selfCNRefsJSON = CommentParser.getSelfCourseNotesReferences(body).toString();
		    	CourseNotes cn = courseNotesController.getCourseNotes(courseNotesId);
		    	List<CourseNotesReference> selfCNRefs = CourseNotesReferenceParser.toList(selfCNRefsJSON, cn);

		    	int id = commentController.addCourseNotesComment(courseNotesId, comment, selfCNRefs, authChecker.getCurrentUserId());

		    	comment = commentController.getComment(id);
		    	response = CommentParser.from(comment, null, selfCNRefs).toString();
				
		    	return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();

    }
    
    /**
     * Edit an existing video comment.
     *
     * @param Id of course that contains video
     * @param Id of lecture that contains video
     * @param Id of video
     * @param Id of the comment to update
     * @param Json string containing the updated comment object
     * @return updated comment
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/video/{videoId}/comment/{commentId}", method = RequestMethod.PATCH)
    public ResponseEntity<String> patchVideoComment(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int videoId, @PathVariable final int commentId, @RequestBody final String body) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "patchVideoComment") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
	    		Comment comment = CommentParser.to(body);
		    	commentController.updateComment(commentId, comment, authChecker.getCurrentUserId());
		    	return new ResponseEntity<String>(HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }

    /**
     * Retrieve all video comments on a certain video.
     *
     * @param Id of course that contains video
     * @param Id of lecture that contains video
     * @param Id of video
     * @return list of comments
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/video/{videoId}/comment", method = RequestMethod.GET)
    public ResponseEntity<String> getVideoComments(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int videoId) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "getVideoComments") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String commentListJSON = "";

		    	List<Comment> commentList = commentController.getVideoComments(videoId);
		    	for(Comment c: commentList) {
			    	for(Reply r: c.getChildren()) {
		        		fillVideoRefs(r, videoId);
			    	}
		    	}
		    	List<List<VideoReference>> videoRefsList = new ArrayList<List<VideoReference>>();
		    	for(Comment a : commentList) {
		    		videoRefsList.add(videoReferenceController.getSelfVideoReferences(videoId, a.getId()));
		    	}
		    	commentListJSON = CommentParser.fromList(commentList, videoRefsList, null).toString();
		    	System.out.println("Answered: "+commentListJSON);
		    	
		    	return new ResponseEntity<String>(commentListJSON, HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
    
    /**
     * Retrieve all comments on certain coursenotes.
     *
     * @param Id of course that contains coursenotes
     * @param Id of lecture that contains coursenotes
     * @param Id of coursenotes
     * @return list of comments
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/coursenotes/{id}/comment", method = RequestMethod.GET)
    public ResponseEntity<String> getCourseNotesComments(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int id) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "getCourseNotesComments") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String response = "";
		    	
		    	List<Comment> commentList = commentController.getCourseNotesComments(id);
		    	for(Comment c: commentList) {
		    		for(Reply r: c.getChildren()) {
		        		fillCnRefs(r, id);
		        	}
		    	}
		    	List<List<CourseNotesReference>> cnRefsList = new ArrayList<List<CourseNotesReference>>();
		    	for(Comment a : commentList) {
		    		cnRefsList.add(courseNotesReferenceController.getSelfCourseNotesReferences(id, a.getId()));
		    	}
		    	response = CommentParser.fromList(commentList, null, cnRefsList).toString();
		    	
		    	return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();
		
    }
    
    private void fillCnRefs(Reply r, int id) throws ClassicDatabaseException, ClassicNotFoundException {
    	List<CourseNotesReference> cnRefs = courseNotesReferenceController.getSelfCourseNotesReferences(id, r.getId());
		r.setSelfCourseNotesReferences(cnRefs);
		for(Reply c: r.getChildren()) {
    		fillCnRefs(c, id);
    	}
    }
    
    /**
     * Retrieve a comment on a certain video.
     *
     * @param Id of course that contains video
     * @param Id of lecture that contains video
     * @param Id of video
     * @return comment object
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/video/{videoId}/comment/{commentId}", method = RequestMethod.GET)
    public ResponseEntity<String> getVideoComment(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int videoId, @PathVariable final int commentId) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "getVideoComment") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String commentJSON = "";
		    	
		    	Comment comment = commentController.getComment(commentId);
		    	for(Reply r: comment.getChildren()) {
	        		fillVideoRefs(r, videoId);
		    	}
		    	List<VideoReference> videoRefs = videoReferenceController.getSelfVideoReferences(videoId, commentId);

		    	commentJSON = CommentParser.from(comment, videoRefs, null).toString();
		    	System.out.println("Answered: "+commentJSON);
		    	
		    	return new ResponseEntity<String>(commentJSON, HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
    
    private void fillVideoRefs(Reply r, int id) throws ClassicDatabaseException, ClassicNotFoundException{
    	List<VideoReference> videoRefs = videoReferenceController.getSelfVideoReferences(id, r.getId());
    	r.setSelfVideoReferences(videoRefs);
		for(Reply c: r.getChildren()) {
    		fillVideoRefs(c, id);
    	}
    }
    
    /**
     * Retrieve a comment on certain coursenotes.
     *
     * @param Id of course that contains coursenotes
     * @param Id of lecture that contains coursenotes
     * @param Id of coursenotes
     * @return comment object
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/coursenotes/{courseNotesId}/comment/{commentId}", method = RequestMethod.GET)
    public ResponseEntity<String> getCourseNotesComment(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int courseNotesId, @PathVariable final int commentId) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "getCourseNotesComment") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String response = "";

		    	Comment comment = commentController.getComment(commentId);
		    	for(Reply r: comment.getChildren()) {
	        		fillCnRefs(r, courseNotesId);
	        	}
		    	List<CourseNotesReference> cnRefs = courseNotesReferenceController.getSelfCourseNotesReferences(courseNotesId, commentId);
		    	response = CommentParser.from(comment, null, cnRefs).toString();
		    	
		    	return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
    
    /**
     * Delete a certain comment.
     *
     * @param Id of course that contains coursenotes
     * @param Id of lecture that contains coursenotes
     * @param Id of coursenotes
     * @param Id of comment
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/coursenotes/{courseNotesId}/comment/{commentId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteCourseNotesComment(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int courseNotesId, @PathVariable final int commentId) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "deleteCourseNotesComment") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				commentController.deleteComment(commentId, authChecker.getCurrentUserId());
		    	return new ResponseEntity<String>(HttpStatus.OK);
			}
		}.runAndHandle();
    }
    
    /**
     * Edit an existing coursenotes comment.
     *
     * @return updated comment
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/coursenotes/{courseNotesId}/comment/{commentId}", method = RequestMethod.PATCH)
    public ResponseEntity<String> patchCourseNotesComment(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int courseNotesId, @PathVariable final int commentId, @RequestBody final String body) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "patchCourseNotesComment") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String response = "";

	    		Comment comment = CommentParser.to(body);
		    	commentController.updateComment(commentId, comment, authChecker.getCurrentUserId());
		    	
		    	comment = commentController.getComment(commentId);
		    	response = CommentParser.from(comment, null, null).toString();
	    	
		    	return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
    
    /**
     * Delete a certain comment.
     *
     * @param Id of course that contains video
     * @param Id of lecture that contains video
     * @param Id of video
     * @param Id of comment
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/video/{videoId}/comment/{commentId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteComment(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int videoId, @PathVariable final int commentId) {
    	return new Runner(exceptionHandler, authChecker, userController, "deleteComment") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				commentController.deleteComment(commentId, authChecker.getCurrentUserId());
		    	return new ResponseEntity<String>(HttpStatus.OK);
			}
		}.runAndHandle();
    }
    
    /**
     * Approve a comment.
     *
     * @param Id of comment
     */
    @RequestMapping(value = "/comment/{commentId}/approve", method = RequestMethod.POST)
    public ResponseEntity<String> approveComment(@PathVariable final int commentId) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "approveComment") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
	    		commentController.setApprovedComment(commentId, true, authChecker.getCurrentUserId());
		    	return new ResponseEntity<String>(HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
    
    /**
     * Unapprove a comment.
     *
     * @param Id of comment
     */
    @RequestMapping(value = "/comment/{commentId}/unapprove", method = RequestMethod.POST)
    public ResponseEntity<String> unapproveComment(@PathVariable final int commentId) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "unapproveComment") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
	    		commentController.setApprovedComment(commentId, false, authChecker.getCurrentUserId());
		    	return new ResponseEntity<String>(HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
    
    /**
     * Upvote a comment.
     *
     * @param Id of comment
     */
    @RequestMapping(value = "/comment/{commentId}/upvote", method = RequestMethod.POST)
    public ResponseEntity<String> upvoteComment(@PathVariable final int commentId) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "upvoteComment") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
	    		commentController.voteComment(commentId, true, authChecker.getCurrentUserId());
		    	return new ResponseEntity<String>(HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
    
    /**
     * Downvote a comment.
     *
     * @param Id of comment
     */
    @RequestMapping(value = "/comment/{commentId}/downvote", method = RequestMethod.POST)
    public ResponseEntity<String> downvoteComment(@PathVariable final int commentId) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "downvoteComment") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
	    		commentController.voteComment(commentId, false, authChecker.getCurrentUserId());
		    	return new ResponseEntity<String>(HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
	
}
