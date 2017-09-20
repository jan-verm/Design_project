package endpoint;

import interfaces.UserControllerInterface;

import java.util.List;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import parser.UserParser;
import security.AuthenticationChecker;
import config.AbstractConfig;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import exceptionhandler.ExceptionHandler;
import exceptionhandler.Runner;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jan Vermeulen
 */
@RestController
public class UserEndpoint {

    private UserControllerInterface userController;
    private ExceptionHandler exceptionHandler;
    private AuthenticationChecker authChecker;
    
    /**
     * Create a new UserEndpoint and initialize controller dependencies.
     *
     * @return UserEndpoint
     */
    @Autowired
    public UserEndpoint(AbstractConfig config) {
    	userController = config.getUserController();
        exceptionHandler = config.getExceptionHandler();
        authChecker = config.getAuthenticationChecker();
    }

    /**
     * Retrieve all users
     *
     * @return List of users
     */
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity<String> getUsers() {
    	
    	return new Runner(exceptionHandler, null, null, "noAuth") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String response = "";

				List<User> userList = userController.getUsers();
				response = UserParser.fromList(userList).toString();
	    	
				return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
	
    /**
     * Add a users
     *
     * @param JSON string containing the new user
     * @return Created user object
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<String> postUser(@RequestBody final String body) {
    	
    	return new Runner(exceptionHandler, null, null, "noAuth") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
                            String response = "";
                            User user = UserParser.to(body);
                            String plainPw = user.getPassword();
                            // The filtering of the following password guarantees lti users can only enter through the safe lti endpoint.
                            if (plainPw.equals("very_secret_lti_user_password")){
                                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                            }
                            
                            String hashedPw = BCrypt.hashpw(plainPw, BCrypt.gensalt());
                            user.setPassword(hashedPw);
                            int id = userController.addUser(user);

                            user = userController.getUser(id);
                            response = UserParser.from(user).toString();

                            return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
    
    /**
     * Retrieve a certain user
     *
     * @param Id of the user
     * @return User object
     */
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public ResponseEntity<String> getUser(@PathVariable final int userId) {
    	
    	return new Runner(exceptionHandler, null, null, "noAuth") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String response = "";
		    	
		    	User user = userController.getUser(userId);
		    	response = UserParser.from(user).toString();
	    	
		    	return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
    
    /**
     * Retrieve a certain user by its name.
     *
     * @param userName name of the user
     * @return User object
     */
    @RequestMapping(value = "/userByName/{userName}", method = RequestMethod.GET)
    public ResponseEntity<String> getUserByName(@PathVariable final String userName) {
    	return new Runner(exceptionHandler, authChecker, userController, "getUserByName") {
                @Override
                public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
                    try {
                        User user = userController.getUser(URLDecoder.decode(userName, "UTF-8"));
                        String response = UserParser.from(user).toString();
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } catch (UnsupportedEncodingException ex) {
                        // This can never happen.
                        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
        }.runAndHandle();
    }
    
    /**
     * Update a certain user
     *
     * @param Id of the user
     * @param JSON string containing the new user
     * @return User object
     */
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.PATCH)
    public ResponseEntity<String> patchUser(@PathVariable final int userId, @RequestBody final String body) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "noAuth") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String response = "";
		    	
				User user = UserParser.to(body);
		    	userController.updateUser(userId, user, authChecker.getCurrentUserId());
		    	
		    	user = userController.getUser(userId);
		    	response = UserParser.from(user).toString();
	    	
		    	return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
    
    /**
     * Delete a certain user
     *
     * @param Id of the user
     */
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteUser(@PathVariable final int userId) {
    	
    	return new Runner(exceptionHandler, authChecker, userController, "noAuth") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				String response = "";
				userController.deleteUser(userId, authChecker.getCurrentUserId());
	    	
				return new ResponseEntity<String>(response, HttpStatus.OK);
			}
		}.runAndHandle();
    	
    }
    
}
