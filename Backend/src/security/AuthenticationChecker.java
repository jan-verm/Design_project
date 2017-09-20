package security;

import interfaces.UserControllerInterface;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import courses.Role;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.exceptions.ClassicUnauthorizedException;

/**
 * This class is a toolkit concerning authentication
 * e.g. session information extraction or method access control
 *
 * @author Jan Vermeulen
 */
public class AuthenticationChecker {

	public static final String AUTH_CONFIG = "authentication.properties";
	private UserControllerInterface userController;
	private Properties prop;

	/**
	 * Create new authentication object.
	 *
	 * @param UserController
	 */
	public AuthenticationChecker(UserControllerInterface userController) {
		this.userController = userController;

		try {
			prop = new Properties();
			InputStream input = getClass().getClassLoader().getResourceAsStream(AUTH_CONFIG);
			prop.load(input);
		} catch (IOException | NullPointerException e) {
			throw new RuntimeException("Could not load authentication config!");
		}
	}

	/**
	 * Retrieve id of the current user.
	 */
	public int getCurrentUserId() throws ClassicDatabaseException, ClassicNotFoundException {
		int id = -1;
		try {
			id = userController.getUser(getCurrentUsername()).getId();
		} catch (ClassicNotFoundException e) {
			throw new ClassicUnauthorizedException("Not logged in!");
		}
		return id;
	}

	/**
	 * Retrieve username of the current user.
	 */
	public String getCurrentUsername() throws ClassicDatabaseException, ClassicNotFoundException {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	/**
	 * Retrieve role of the current user.
	 */
	@SuppressWarnings("unchecked")
	public String getCurrentRole() {
		// There will always be only one authority
		GrantedAuthority authority = ((List<GrantedAuthority>) SecurityContextHolder.getContext()
				.getAuthentication().getAuthorities()).get(0);
		return authority.getAuthority();
	}

	/**
	 * In authentication.properties, a specification can be found for every endpoint,
	 * which roles have access to which endpoint.
	 * This method reads the correct roles for a given endpoint and checks if in contains the requested role.
	 */
	public void checkMethodAccess(String method, User user) {
		String[] authenticatedRoles = prop.getProperty(method).split(",");
		Role role = user.getRole();
		if(!lowerCaseList(Arrays.asList(authenticatedRoles)).contains(role.toString().toLowerCase())) {
			throw new ClassicUnauthorizedException("User "+user.getUsername()+" does not have access to "+method+"!");
		}
	}

	private List<String> lowerCaseList(List<String> list) {
		ListIterator<String> iterator = list.listIterator();
		while (iterator.hasNext())
		{
			iterator.set(iterator.next().toLowerCase());
		}
		return list;
	}

}
