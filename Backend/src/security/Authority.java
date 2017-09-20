package security;

import org.springframework.security.core.GrantedAuthority;

import courses.Role;

/**
 * Authority is the spring equivalent of a Role.
 *
 * @author Jan Vermeulen
 */
public class Authority implements GrantedAuthority {

	private static final long serialVersionUID = 1L;
	
	private String role;
	
	public Authority(Role role) {
		this.role=role.toString();
	}
	
	@Override
	public String getAuthority() {
		return role;
	}
}
