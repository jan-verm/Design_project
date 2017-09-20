package exceptionhandler;

import interfaces.UserControllerInterface;

import org.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import security.AuthenticationChecker;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.exceptions.ClassicUnauthorizedException;
import exceptions.ClassicCharacterNotAllowedException;

/**
 * This abstract class is used to avoid code duplication in the endpoints.
 * Every endpoint creates a new object of this class, overriding the action() method.
 * After object creation, runAndHandle() is called.
 * This will make sure that all exceptions thrown in the action() method will be caught
 * in a uniform way, without code duplication.
 * Since every endpoint has to check if the user is authorized to address it,
 * the authorization check is also done in runAndHandle().
 * 
 * @author Jan Vermeulen
 */
public abstract class Runner {

	private ExceptionHandler eh;
	private String method;
	private AuthenticationChecker authChecker;
	private UserControllerInterface userController;

	/**
	 * Make a new runner object. Initialize variables.
	 * 
	 * @param An exceptionhandler object, caught exceptions will be handled by this class.
	 * @param The method asking authorization for (a String).
	 * @param An authChecker object. This will be used to check authorization.
	 * @param A userController object.
	 */
	public Runner(ExceptionHandler eh, AuthenticationChecker authChecker, UserControllerInterface userController, String method) {
		this.eh = eh;
		this.method = method;
		this.authChecker = authChecker;
		this.userController = userController;
	}

	/**
	 * This method should be overridden in the endpoints, containing all endpoint logic.
	 */
	public abstract ResponseEntity<String> action() throws ClassicDatabaseException, ClassicNotFoundException, JSONException;


	/**
	 * Execute action, handling exceptions and checking authorization.
	 */
	public ResponseEntity<String> runAndHandle() {
		// Check if user has authorization for action
		if(!method.equals("noAuth")) {
			try {
				User user = userController.getUser(authChecker.getCurrentUserId());
				authChecker.checkMethodAccess(method, user);
			} catch (ClassicDatabaseException e) {
				return eh.handle(e);
			} catch (ClassicNotFoundException e) {
				return eh.handle(e);
			} catch (ClassicUnauthorizedException e) {
				return eh.handle(e);
			}
		}

		// Execute implemented action
		try {
			return action();
		} catch (ClassicDatabaseException e) {
			return eh.handle(e);
		} catch (ClassicNotFoundException e) {
			return eh.handle(e);
		} catch (JSONException e) {
			return eh.handle(e);
		} catch (BadCredentialsException e) {
			return eh.handle(e);
		} catch (ClassicUnauthorizedException e) {
			return eh.handle(e);
		} catch (ClassicCharacterNotAllowedException e) {
			return eh.handle(e);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("runAndHandle failed: "+e.toString());
		}
	}
}
