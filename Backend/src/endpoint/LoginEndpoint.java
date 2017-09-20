package endpoint;

import interfaces.UserControllerInterface;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import parser.LoginParser;
import security.AuthenticationChecker;
import config.AbstractConfig;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.exceptions.ClassicUnauthorizedException;
import exceptionhandler.ExceptionHandler;
import exceptionhandler.Runner;


/**
 * @author Jan Vermeulen
 */
@RestController
public class LoginEndpoint {

	@Autowired
	@Qualifier("classicAuthenticationManager")
	AuthenticationManager authManager;
	
    private ExceptionHandler exceptionHandler;
    private UserControllerInterface userController;
    private AuthenticationChecker authChecker;
    
    /**
     * Create a new LoginEndpoint and initialize controller dependencies.
     *
     * @return LoginEndpoint
     */
    @Autowired
    public LoginEndpoint(AbstractConfig config) {
        exceptionHandler = config.getExceptionHandler();
        userController = config.getUserController();
        authChecker = config.getAuthenticationChecker();
    }
	
    /**
     * Authenticate with username and password, afterwards start a new session
     * 
     * @param Json string containing user credentials
     * @return Id of the session, username, password
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<String> postCredentials(@RequestBody final String body) {
		
		return new Runner(exceptionHandler, null, null, "noAuth") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
                            String password = LoginParser.getPassword(body);
                            // The filtering of the following password guarantees lti users can only enter through the safe lti endpoint.
                            if (password.equals("very_secret_lti_user_password")){
                                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                            }
                            UsernamePasswordAuthenticationToken token = 
                                            new UsernamePasswordAuthenticationToken(LoginParser.getUsername(body), password);
                            Authentication auth = authManager.authenticate(token);
                            SecurityContextHolder.getContext().setAuthentication(auth);
                            String sessionId = RequestContextHolder.getRequestAttributes().getSessionId();

                            return new ResponseEntity<String>(LoginParser.buildCredentials(authChecker.getCurrentUsername(), 
                                            authChecker.getCurrentUserId(), authChecker.getCurrentRole(), sessionId), HttpStatus.OK);
			}
		}.runAndHandle();
	}

    /**
     * Check whether the user is authenticated and retrieve session information
     * 
     * @param Json string containing user credentials
     * @return Id of the session, username, password
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
	public ResponseEntity<String> checkCredentials() {

		return new Runner(exceptionHandler, null, null, "noAuth") {
			@Override
			public ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException {
				Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
				if(auth==null || !auth.isAuthenticated()) {
					return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
				}
				String sessionId = RequestContextHolder.getRequestAttributes().getSessionId();
				
				int userId = 0;
				try {
					 userId = authChecker.getCurrentUserId();
				} catch(ClassicUnauthorizedException e) {
					// Do nothing, just return userId = 0;
				}
				
				return new ResponseEntity<String>(LoginParser.buildCredentials(authChecker.getCurrentUsername(), 
						userId, authChecker.getCurrentRole().toString(), sessionId), HttpStatus.OK);
			}
		}.runAndHandle();
	}
	
    /**
     * Log out. Clear all session information
     * 
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
	public ResponseEntity<String> logout() {
		SecurityContextHolder.clearContext();
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
}
