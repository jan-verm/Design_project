package endpoint;

import interfaces.CourseControllerInterface;
import interfaces.UserControllerInterface;

import java.util.List;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import parser.LectureParser;
import security.AuthenticationChecker;
import config.AbstractConfig;
import courses.Lecture;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import exceptionhandler.ExceptionHandler;
import exceptionhandler.Runner;

/**
 * @author Jan Vermeulen
 */
@RestController
public class LectureEndpoint {

    private CourseControllerInterface courseController;
    private ExceptionHandler exceptionHandler;
    private AuthenticationChecker authChecker;
    private UserControllerInterface userController;
    
    /**
     * Create a new LectureEndpoint and initialize controller dependencies.
     *
     * @return LectureEndpoint
     */
    @Autowired
    public LectureEndpoint(AbstractConfig config) {
    	courseController = config.getCourseController();
    	exceptionHandler = config.getExceptionHandler();
    	authChecker = config.getAuthenticationChecker();
    	userController = config.getUserController();
    }
	
    /**
     * Get all lectures corrensponding to a certain course.
     * 
     * @param Id of the course
     * @return List of lectures
     */
    @RequestMapping(value = "/course/{courseId}/lecture", method = RequestMethod.GET)
    public ResponseEntity<String> getLectures(@PathVariable final int courseId) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "getLectures") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String response = "";
		    	
				String owner = courseController.getCourse(courseId).getOwner().getUsername();
				List<Lecture> lectures = courseController.getLectures(courseId);
				response = LectureParser.fromList(lectures, owner).toString();
	    	
				return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
	
    /**
     * Get a certain lecture object.
     * 
     * @param Id of the course
     * @param Id of the Lecture
     * @return Lecture object
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}", method = RequestMethod.GET)
    public ResponseEntity<String> getLecture(@PathVariable final int courseId, @PathVariable final int lectureId) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "getLecture") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String response = "";
		    	
				String owner = courseController.getCourse(courseId).getOwner().getUsername();
				Lecture lecture = courseController.getLecture(lectureId);
				response = LectureParser.from(lecture, owner).toString();
	    	
				return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
	
    /**
     * Create a new lecture inside a certain course
     * 
     * @param Id of the course
     * @param Json string containing the new lecture object
     * @return Lecture object
     */
    @RequestMapping(value = "/course/{courseId}/lecture", method = RequestMethod.POST)
    public ResponseEntity<String> postLecture(@PathVariable final int courseId, @RequestBody final String body) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "postLecture") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String response = "";
		    	
	    		Lecture lecture = LectureParser.to(body);
				int id = courseController.addLecture(courseId, lecture, authChecker.getCurrentUserId());
				
				String owner = courseController.getCourse(courseId).getOwner().getUsername();
				response = LectureParser.from(courseController.getLecture(id), owner).toString();
	    	
				return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
	
    /**
     * Update a lecture
     * 
     * @param Id of the course containing the lecture
     * @param Id of the lecture
     * @param Json string containing the updated lecture object
     * @return Lecture object
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}", method = RequestMethod.PATCH)
    public ResponseEntity<String> patchLecture(@PathVariable final int courseId, @PathVariable final int lectureId, @RequestBody final String body) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "patchLecture") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String response = "";
		    	
	    		Lecture lecture = LectureParser.to(body);
				courseController.updateLecture(lectureId, lecture, authChecker.getCurrentUserId());
				

				String owner = courseController.getCourse(courseId).getOwner().getUsername();
				response = LectureParser.from(courseController.getLecture(lectureId), owner).toString();
	    	
				return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();
    }
	
    /**
     * Delete a lecture
     * 
     * @param Id of the course containing the lecture
     * @param Id of the lecture
     */
    @RequestMapping(value = "/course/{courseId}/lecture/{lectureId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteLecture(@PathVariable final int courseId, @PathVariable final int lectureId) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "deleteLecture") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String response = "";
		    	
				courseController.deleteLecture(lectureId, authChecker.getCurrentUserId());
				response = "{ \"message\" : \"Succesfully deleted.\"}";
	    	
				return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
	
}
