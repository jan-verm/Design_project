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

import parser.CourseParser;
import security.AuthenticationChecker;
import config.AbstractConfig;
import courses.Course;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import exceptionhandler.ExceptionHandler;
import exceptionhandler.Runner;

/**
 * @author Jan Vermeulen
 */
@RestController
public class CourseEndpoint {

    private CourseControllerInterface courseController;
    private ExceptionHandler exceptionHandler;
    private AuthenticationChecker authChecker;
    private UserControllerInterface userController;
    
    /**
     * Create a new CourseEndpoint and initialize controller dependencies.
     *
     * @return CourseEndpoint
     */
    @Autowired
    public CourseEndpoint(AbstractConfig config) {
    	courseController = config.getCourseController();
        exceptionHandler = config.getExceptionHandler();
        authChecker = config.getAuthenticationChecker();
        userController = config.getUserController();
    }
	
    /**
     * Get all courses.
     *
     * @return list of courses
     */
    @RequestMapping(value = "/course", method = RequestMethod.GET)
    public ResponseEntity<String> getCourses() {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "getCourses") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String response = "";
		    	
				List<Course> courses = courseController.getCourses();
				response = CourseParser.fromList(courses).toString();
	    	
				return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();

    }
	
    /**
     * Get a certain course.
     * 
     * @param Id of the course
     * @return course object
     */
    @RequestMapping(value = "/course/{courseId}", method = RequestMethod.GET)
    public ResponseEntity<String> getCourse(@PathVariable final int courseId) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "getCourse") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String response = "";

				Course course = courseController.getCourse(courseId);
				response = CourseParser.from(course).toString();
	    	
				return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();
    }
	
    /**
     * Create a new course.
     * 
     * @param Json string containing the course object
     * @return course object
     */
    @RequestMapping(value = "/course", method = RequestMethod.POST)
    public ResponseEntity<String> postCourse(@RequestBody final String body) {
    	return new Runner(exceptionHandler, authChecker, userController, "postCourse") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String response = "";
	    		Course course = CourseParser.to(body);
				int id = courseController.addCourse(course, authChecker.getCurrentUserId());
				
				response = CourseParser.from(courseController.getCourse(id)).toString();
	    	
				return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
	
    /**
     * Subscribe to a certain course.
     * 
     * @param Id of the course
     */
    @RequestMapping(value = "/course/{courseId}/subscribe", method = RequestMethod.POST)
    public ResponseEntity<String> subscribeCourse(@PathVariable final int courseId) {
    	return new Runner(exceptionHandler, authChecker, userController, "subscribeCourse") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				userController.subscribeCourse(authChecker.getCurrentUserId(), courseId);
				return new ResponseEntity<String>(HttpStatus.OK);
			}
		}.runAndHandle();	
    }
	
    /**
     * Unsubscribe from a certain course.
     * 
     * @param Id of the course
     */
    @RequestMapping(value = "/course/{courseId}/unsubscribe", method = RequestMethod.POST)
    public ResponseEntity<String> unsubscribeCourse(@PathVariable final int courseId) {
    	return new Runner(exceptionHandler, authChecker, userController, "subscribeCourse") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				userController.unSubscribeCourse(authChecker.getCurrentUserId(), courseId);
				return new ResponseEntity<String>(HttpStatus.OK);
			}
		}.runAndHandle();	
    }
	
    /**
     * Update from a certain course.
     * 
     * @param Id of the course
     * @return updated course object
     */
    @RequestMapping(value = "/course/{courseId}", method = RequestMethod.PATCH)
    public ResponseEntity<String> patchCourse(@PathVariable final int courseId, @RequestBody final String body) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "patchCourse") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String response = "";
		    	
	    		Course course = CourseParser.to(body);
				courseController.updateCourse(courseId, course, authChecker.getCurrentUserId());
				
				response = CourseParser.from(courseController.getCourse(courseId)).toString();
	    	
				return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();
		
    }
	
    /**
     * Delete a certain course.
     * 
     * @param Id of the course
     */
    @RequestMapping(value = "/course/{courseId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteCourse(@PathVariable final int courseId) {
    	return new Runner(exceptionHandler, authChecker, userController, "deleteCourse") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String response = "";
				courseController.deleteCourse(courseId, authChecker.getCurrentUserId());
				response = "{ \"message\" : \"Succesfully deleted.\"}";
			
				return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();
    }
    
}
