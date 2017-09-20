package security;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Global spring security config file.
 * The userdetailsservice and passwordhasher are configured here.
 *
 * @author Jan Vermeulen
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Resource(name="authService")
	private UserDetailsService userDetailsService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	    auth.userDetailsService(userDetailsService).passwordEncoder(new PasswordHasher());
	}

	/**
	 * Disable the default spring login system, using forms.
	 * We want to use json requests.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.logout().disable()
	    .authorizeRequests()
	    	.anyRequest().permitAll()
	        .and()
	    .formLogin().disable()
	    .httpBasic().disable()
	    .csrf().disable();
	}
	
	@Bean(name="classicAuthenticationManager")
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
}
