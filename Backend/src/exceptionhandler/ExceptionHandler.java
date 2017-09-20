package exceptionhandler;

import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNameTakenException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.exceptions.ClassicUnauthorizedException;
import exceptions.ClassicCharacterNotAllowedException;

/**
 * @author Jan
 */
public class ExceptionHandler {

	/**
	 * Handle all ClassicNotFoundExceptions, occurring in the endpoints
	 *
	 * @return Response containing a BAD_REQUEST error
	 */
	public ResponseEntity<String> handle(ClassicNotFoundException e) {
		e.printStackTrace();
		return new ResponseEntity<String>(e.toString(), HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle all JSONExceptions, occurring in the endpoints
	 *
	 * @return Response containing a BAD_REQUEST error
	 */
	public ResponseEntity<String> handle(JSONException e) {
		e.printStackTrace();
		return new ResponseEntity<String>(e.toString(), HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle all BadCredentialsExceptions, occurring in the endpoints
	 *
	 * @return Response containing a UNAUTHORIZED error
	 */
	public ResponseEntity<String> handle(BadCredentialsException e) {
		e.printStackTrace();
		return new ResponseEntity<String>(e.toString(), HttpStatus.UNAUTHORIZED);
	}

	/**
     * Handle all ClassicNameTakenExceptions, occurring in the endpoints
     *
     * @return Response containing a BAD_REQUEST error
     */
	public ResponseEntity<String> handle(ClassicNameTakenException e) {
		e.printStackTrace();
		return new ResponseEntity<String>(e.toString(), HttpStatus.BAD_REQUEST);
	}

	/**
     * Handle all ClassicCharacterNotAllowedExceptions, occurring in the endpoints
     *
     * @return Response containing a BAD_REQUEST error
     */
	public ResponseEntity<String> handle(ClassicCharacterNotAllowedException e) {
		e.printStackTrace();
		return new ResponseEntity<String>(e.toString(), HttpStatus.BAD_REQUEST);
	}

	/**
     * Handle all ClassicDatabaseExceptions, occurring in the endpoints
     *
     * @return Response containing a INTERNAL_SERVER_ERROR error
     */
	public ResponseEntity<String> handle(ClassicDatabaseException e) {
		e.printStackTrace();
		return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
     * Handle all ClassicUnauthorizedExceptions, occurring in the endpoints
     *
     * @return Response containing a UNAUTHORIZED error
     */
	public ResponseEntity<String> handle(ClassicUnauthorizedException e) {
		e.printStackTrace();
		return new ResponseEntity<String>(e.toString(), HttpStatus.UNAUTHORIZED);
	}

	/**
     * Handle all RuntimeExceptions, occurring in the endpoints
     *
     * @return Response containing a INTERNAL_SERVER_ERROR error
     */
	public ResponseEntity<String> handle(RuntimeException e) {
		e.printStackTrace();
		return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
	}



}
