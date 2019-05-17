package de.ffm.rka.rkareddit.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * This is a cnfiguration service for authentication
 * @author rka
 *
 */
@Configuration
@EnableWebSecurity
public class SecConfig extends WebSecurityConfigurerAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecConfig.class);
	private static final String ADMIN="ADMIN";
	private static final String USER="USER";
	private static final String DBA="DBA";
	private static final String ACTUATOR="ACTUATOR";
	
	
	
	@Autowired
	private UserDetailsServiceImpl userDetalsService;
		
	@Override
	public void configure(HttpSecurity http) throws Exception {
		final int oneDay = 86400;
		http.authorizeRequests().requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole(ACTUATOR)
								.antMatchers("/").permitAll()
								.antMatchers("/links/link/create").hasRole(USER)
								.antMatchers("/h2_console/**").hasRole(DBA)
 								.antMatchers("/links/").permitAll()
								.and()
							.formLogin()
								.loginPage("/login").permitAll()
								.usernameParameter("email")
								.defaultSuccessUrl("/links")
							.and()
							.logout()
								.invalidateHttpSession(true)
								.clearAuthentication(true)
							.and()
							.rememberMe()
								.key("uniqueAndSecret")
								.tokenValiditySeconds(oneDay);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetalsService);
	}
}
