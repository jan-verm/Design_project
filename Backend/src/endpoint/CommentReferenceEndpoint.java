package endpoint;

import interfaces.CourseNotesControllerInterface;
import interfaces.CourseNotesReferenceControllerInterface;
import interfaces.UserControllerInterface;
import interfaces.VideoControllerInterface;
import interfaces.VideoReferenceControllerInterface;

import java.util.List;

import models.CourseNotes;
import models.CourseNotesReference;
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

import parser.CourseNotesReferenceParser;
import parser.VideoReferenceParser;
import security.AuthenticationChecker;
import config.AbstractConfig;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import exceptionhandler.ExceptionHandler;
import exceptionhandler.Runner;
import org.json.JSONArray;

/**
 * @author Jan Vermeulen
 */
@RestController
public class CommentReferenceEndpoint {

	private VideoControllerInterface videoController;
	private VideoReferenceControllerInterface videoReferenceController;
	private CourseNotesControllerInterface courseNotesController;
	private CourseNotesReferenceControllerInterface courseNotesReferenceController;
	private UserControllerInterface userController;
    private ExceptionHandler exceptionHandler;
    private AuthenticationChecker authChecker;

    /**
     * Create a new ReferenceEndpoint and initialize controller dependencies.
     *
     * @return ReferenceEndpoint
     */
    @Autowired
	public CommentReferenceEndpoint(AbstractConfig config) {
		videoController = config.getVideoController();
		videoReferenceController = config.getVideoReferenceController();
		courseNotesController = config.getCourseNotesController();
		courseNotesReferenceController = config.getCourseNotesReferenceController();
		userController = config.getUserController();
		exceptionHandler = config.getExceptionHandler();
		authChecker = config.getAuthenticationChecker();
	}

    /**
     * Get all video references from a certain comment.
     *
     * @param Id of the course
     * @param Id of the lecture
     * @param Id of the comment
     * @return list of video references
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/comment/{commentId}/videoref", method = RequestMethod.GET)
	public ResponseEntity<String> getVideoReferences(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int commentId) {
		
		return new Runner(exceptionHandler, authChecker, userController, "getVideoReferences") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String refListJSON = "";

				List<VideoReference> videoRefs = videoReferenceController.getVideoReferences(commentId);
				//refListJSON = VideoReferenceParser.fromList(videoRefs).toString();
                                JSONArray jsonArray = new JSONArray();
                                for (VideoReference ref : videoRefs) {
                                    int [] parents = videoReferenceController.getParents(ref.getVideo().getId());
                                    jsonArray.put(VideoReferenceParser.from(ref, parents));
                                }
                                refListJSON = jsonArray.toString();
				System.out.println("Answered: "+refListJSON);

				return new ResponseEntity<String>(refListJSON, HttpStatus.OK);
			}
		}.runAndHandle();
		
	}

    /**
     * Get all coursenotes references from a certain comment
     *
     * @param Id of the course
     * @param Id of the lecture
     * @param Id of the comment
     * @return list of video references
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/comment/{commentId}/coursenotesref", method = RequestMethod.GET)
	public ResponseEntity<String> getCourseNotesReferences(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int commentId) {
		
		return new Runner(exceptionHandler, authChecker, userController, "getCourseNotesReferences") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String response = "";

				List<CourseNotesReference> list = courseNotesReferenceController.getCourseNotesReferences(commentId);
				//response = CourseNotesReferenceParser.fromList(list).toString();
                                JSONArray jsonArray = new JSONArray();
                                for (CourseNotesReference ref : list) {
                                    int [] parents = courseNotesReferenceController.getParents(ref.getCourseNotes().getId());
                                    jsonArray.put(CourseNotesReferenceParser.from(ref, parents));
                                }
                                response = jsonArray.toString();

				return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();
		
	}

    /**
     * Delete a certain video reference
     *
     * @param Id of the course
     * @param Id of the lecture
     * @param Id of the comment
     * @param Id of the video reference
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/comment/{commentId}/videoref/{videoRefId}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteVideoReference(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int commentId, @PathVariable final int videoRefId) {
		
		return new Runner(exceptionHandler, authChecker, userController, "deleteVideoReference") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				videoReferenceController.deleteVideoReference(commentId, videoRefId, authChecker.getCurrentUserId());
				return new ResponseEntity<String>(HttpStatus.OK);
			}
		}.runAndHandle();
		
	}

    /**
     * Delete a certain coursenotes reference
     *
     * @param Id of the course
     * @param Id of the lecture
     * @param Id of the comment
     * @param Id of the coursenotes reference
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/comment/{commentId}/coursenotesref/{courseNotesRefId}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteCourseNotesReference(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int commentId, @PathVariable final int courseNotesRefId) {
		
		return new Runner(exceptionHandler, authChecker, userController, "deleteCourseNotesReference") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				courseNotesReferenceController.deleteCourseNotesReference(commentId, courseNotesRefId, authChecker.getCurrentUserId());
				return new ResponseEntity<String>(HttpStatus.OK);
			}
		}.runAndHandle();
		
	}

    /**
     * Update a certain video reference
     *
     * @param Id of the course
     * @param Id of the lecture
     * @param Id of the comment
     * @param Id of the video reference
     * @return Updated video reference
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/comment/{commentId}/videoref/{refId}", method = RequestMethod.PATCH)
	public ResponseEntity<String> patchVideoReference(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int commentId, @PathVariable final int refId, @RequestBody final String body) {
		
		return new Runner(exceptionHandler, authChecker, userController, "patchVideoReference") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				Video video = null;
				VideoReference videoRef = null;

				int videoId = VideoReferenceParser.getVideoId(body);
				video = videoController.getVideo(videoId);
				videoRef = VideoReferenceParser.to(body, video);
				videoReferenceController.updateVideoReference(commentId, refId, videoRef, authChecker.getCurrentUserId());

				return new ResponseEntity<String>(HttpStatus.OK);
			}
		}.runAndHandle();
		
	}

    /**
     * Update a certain coursenotes reference
     *
     * @param Id of the course
     * @param Id of the lecture
     * @param Id of the comment
     * @param Id of the coursenotes reference
     * @return Updated coursenotes reference
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/comment/{commentId}/coursenotesref/{courseNotesRefId}", method = RequestMethod.PATCH)
	public ResponseEntity<String> patchCourseNotesReference(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int commentId, @PathVariable final int courseNotesRefId, @RequestBody final String body) {
		
		return new Runner(exceptionHandler, authChecker, userController, "patchCourseNotesReference") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				int courseNotesId = CourseNotesReferenceParser.getCourseNotesId(body);
				
				CourseNotes cn = courseNotesController.getCourseNotes(courseNotesId);
				CourseNotesReference courseNotesRef = CourseNotesReferenceParser.to(body, cn);
				courseNotesReferenceController.updateCourseNotesReference(commentId, courseNotesRefId, courseNotesRef, authChecker.getCurrentUserId());

				return new ResponseEntity<String>(HttpStatus.OK);
			}
		}.runAndHandle();

	}

    /**
     * Add a new video reference
     *
     * @param Id of the course
     * @param Id of the lecture
     * @param Id of the comment
     * @param JSON string containing the new video reference 
     * @return New video reference
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/comment/{commentId}/videoref", method = RequestMethod.POST)
	public ResponseEntity<String> postVideoReference(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int commentId, @RequestBody final String body) {
		
		return new Runner(exceptionHandler, authChecker, userController, "postVideoReference") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String response = "";
				
				int videoId = VideoReferenceParser.getVideoId(body);
				
				// Add new video reference
				Video video = videoController.getVideo(videoId);
				VideoReference videoRef = VideoReferenceParser.to(body, video);
				int id = videoReferenceController.addVideoReference(videoId, commentId, videoRef, authChecker.getCurrentUserId());
				
				// Return newly created video reference
				videoRef = videoReferenceController.getVideoReference(id);
                                int[] parents = videoReferenceController.getParents(videoId);
				response = VideoReferenceParser.from(videoRef, parents).toString();
			
				return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();
		
	}

    /**
     * Add a new coursenotes reference
     *
     * @param Id of the course
     * @param Id of the lecture
     * @param Id of the comment
     * @param JSON string containing the new coursenotes reference 
     * @return New coursenotes reference
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}/comment/{commentId}/coursenotesref", method = RequestMethod.POST)
	public ResponseEntity<String> postCourseNotesReference(@PathVariable final int courseId, @PathVariable final int lectureId, @PathVariable final int commentId, @RequestBody final String body) {
		
		return new Runner(exceptionHandler, authChecker, userController, "postCourseNotesReference") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String response = "";

				int courseNotesId = CourseNotesReferenceParser.getCourseNotesId(body);
				
				// Add new courseNotes reference
				CourseNotes cn = courseNotesController.getCourseNotes(courseNotesId);
				CourseNotesReference courseNotesRef = CourseNotesReferenceParser.to(body, cn); 	
				int id = courseNotesReferenceController.addCourseNotesReference(courseNotesId, commentId, courseNotesRef, authChecker.getCurrentUserId());
				
				// Return newly created courseNotes reference
				courseNotesRef = courseNotesReferenceController.getCourseNotesReference(id);
                                int[] parents = courseNotesReferenceController.getParents(courseNotesId);
				response = CourseNotesReferenceParser.from(courseNotesRef, parents).toString();

				return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();
		
	}

}
