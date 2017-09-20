package security;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * This class is plugged into the spring authentication system
 * and hashes the plain text passwords.
 *
 * @author Jan Vermeulen
 */
public class PasswordHasher implements PasswordEncoder{

	/**
	 * Hash a given password using a bcrypt with a generated salt.
	 * 
	 * @param plain text password
	 */
	@Override
	public String encode(CharSequence plainTextPassword) {
		return BCrypt.hashpw(plainTextPassword.toString(), BCrypt.gensalt());
	}

	/**
	 * Hash plainTextPassword, compare to hashedPassword (retrieved from the database)
	 * 
	 * @param plain text password
	 * @param hashed password
	 * @return boolean
	 */
	@Override
	public boolean matches(CharSequence plainTextPassword, String hashedPassword) {
            return BCrypt.checkpw(plainTextPassword.toString(), hashedPassword);
	}

}