package security;

import config.AbstractConfig;
import interfaces.UserControllerInterface;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;

/**
 * ClassicUserDetailsService connects the spring authentication system and our user controllers,
 * to be able to do spring authentication with our user system.
 *
 * @author Jan Vermeulen
 */
@Service("authService")
public class ClassicUserDetailsService implements UserDetailsService {

	private UserControllerInterface userController;

	@Autowired
	public ClassicUserDetailsService(AbstractConfig config) {
		userController = config.getUserController();
	}

	/**
	 * Retrieve users, return spring users
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			User user = userController.getUser(username);

			List<GrantedAuthority> authorities = new ArrayList<>();
			authorities.add(new Authority(user.getRole()));

			return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
		} catch (ClassicDatabaseException e) {
			throw new UsernameNotFoundException("Internal database problem.");
		} catch (ClassicNotFoundException e) {
			throw new UsernameNotFoundException("Username not found.");
		}
	}

}

