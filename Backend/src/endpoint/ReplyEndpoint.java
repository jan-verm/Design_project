package endpoint;

import interfaces.CommentControllerInterface;
import interfaces.UserControllerInterface;
import models.CourseNotes;
import models.CourseNotesReference;
import models.Reply;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import parser.CourseNotesReferenceParser;
import parser.ReplyParser;
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
public class ReplyEndpoint {

    private CommentControllerInterface commentController;
    private UserControllerInterface userController;
    private ExceptionHandler exceptionHandler;
    private AuthenticationChecker authChecker;
    
    /**
     * Create a new ReplyEndpoint and initialize controller dependencies.
     *
     * @return ReplyEndpoint
     */
    @Autowired
    public ReplyEndpoint(AbstractConfig config) {
    	commentController = config.getCommentController();
    	userController = config.getUserController();
    	exceptionHandler = config.getExceptionHandler();
    	authChecker = config.getAuthenticationChecker();
    }
    
    /**
     * Add a reply to a certain comment
     * 
     * @param Id of the course
     * @param Id of the lecture
     * @param Id of the comment
     * @param JSON string containing the new reply
     * @return New reply object
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/comment/{commentId}/children", method = RequestMethod.POST)
    public ResponseEntity<String> postCommentReply(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int commentId, @RequestBody final String body) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "postCommentReply") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String response = "";
		    	
	        	Reply reply = ReplyParser.to(body);
				int replyId = commentController.addReplytoComment(commentId, reply, authChecker.getCurrentUserId());
				
				commentController.getReply(replyId);
	        	response = ReplyParser.from(reply).toString();

	        	return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
    
    /**
     * Add a reply to a certain reply
     * 
     * @param Id of the course
     * @param Id of the lecture
     * @param Id of the reply
     * @param JSON string containing the new reply
     * @return New reply object
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/reply/{replyId}/children", method = RequestMethod.POST)
    public ResponseEntity<String> postReplyReply(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int replyId, @RequestBody final String body) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "postReplyReply") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String response = "";

	        	Reply reply = ReplyParser.to(body);
	        	int newReplyId = commentController.addReplytoReply(replyId, reply, authChecker.getCurrentUserId());
	        	
	        	commentController.getReply(newReplyId);
	        	response = ReplyParser.from(reply).toString();

	        	return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
    
    /**
     * Delete a reply
     * 
     * @param Id of the course
     * @param Id of the lecture
     * @param Id of the reply
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/reply/{replyId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteReply(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int replyId) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "deleteReply") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				commentController.deleteReply(replyId, authChecker.getCurrentUserId());
		    	return new ResponseEntity<String>(HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
    
    /**
     * Update a reply.
     * 
     * @param Id of the course
     * @param Id of the lecture
     * @param Id of the reply
     * @param JSON string containing the updated reply
     * @return Updated reply object
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/reply/{replyId}", method = RequestMethod.PATCH)
    public ResponseEntity<String> patchReply(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int replyId, @RequestBody final String body) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "patchReply") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String response = "";
		    	
	        	Reply reply = ReplyParser.to(body);
				commentController.updateReply(replyId, reply, authChecker.getCurrentUserId());

	        	reply = commentController.getReply(replyId);
	        	response = ReplyParser.from(reply).toString();

	        	return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
    
    /**
     * Approve a reply.
     * 
     * @param Id of the reply
     */
    @RequestMapping(value = "/reply/{replyId}/approve", method = RequestMethod.POST)
    public ResponseEntity<String> approveReply(@PathVariable final int replyId) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "approveReply") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
	    		commentController.setApprovedReply(replyId, true, authChecker.getCurrentUserId());
		    	return new ResponseEntity<String>(HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
    
    /**
     * Unapprove a reply.
     * 
     * @param Id of the reply
     */
    @RequestMapping(value = "/reply/{replyId}/unapprove", method = RequestMethod.POST)
    public ResponseEntity<String> unapproveReply(@PathVariable final int replyId) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "unapproveReply") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
	    		commentController.setApprovedReply(replyId, false, authChecker.getCurrentUserId());
		    	return new ResponseEntity<String>(HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
    
    /**
     * Upvote a reply.
     * 
     * @param Id of the reply
     */
    @RequestMapping(value = "/reply/{replyId}/upvote", method = RequestMethod.POST)
    public ResponseEntity<String> upvoteReply(@PathVariable final int replyId) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "upvoteReply") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
	    		commentController.voteReply(replyId, true, authChecker.getCurrentUserId());
		    	return new ResponseEntity<String>(HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
    
    /**
     * Downvote a reply.
     * 
     * @param Id of the reply
     */
    @RequestMapping(value = "/reply/{replyId}/downvote", method = RequestMethod.POST)
    public ResponseEntity<String> downvoteReply(@PathVariable final int replyId) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "downvoteReply") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
	    		commentController.voteReply(replyId, false, authChecker.getCurrentUserId());
		    	return new ResponseEntity<String>(HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }

    
}
