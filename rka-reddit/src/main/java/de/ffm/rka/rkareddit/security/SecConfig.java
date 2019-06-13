package de.ffm.rka.rkareddit.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.zaxxer.hikari.HikariDataSource;

import de.ffm.rka.rkareddit.util.BeanUtil;

/**
 * This is a cnfiguration service for authentication
 * @author rka
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecConfig extends WebSecurityConfigurerAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecConfig.class);
	
	@Autowired
	private HikariDataSource dataSource;
	
	@Autowired
	private UserDetailsServiceImpl userDetalsService;
		
	@Value("${userData}")
	private String userData;
	
	@Value("${userAuthorities}")
	private String userAuthorities;
	
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		final int oneDay = 86400;
		http.authorizeRequests().antMatchers("/").permitAll()		
								.antMatchers( "/resources/**").permitAll()
								.antMatchers("/links/").permitAll()
								.requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole(Role.ACTUATOR)
								.antMatchers("/links/link/create").hasRole(Role.ADMIN)
								.antMatchers("/data/h2-console/**").hasRole(Role.DBA)
								
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
								.tokenValiditySeconds(oneDay)
							 .and()
						 		.csrf().disable()
							 	.headers().frameOptions().disable();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		LOGGER.info("Datasurce url: {}",dataSource.getJdbcUrl());
		LOGGER.info("USER-AUTHORITIES: {}",userAuthorities);
		auth
		.jdbcAuthentication()
		.usersByUsernameQuery(userData)
		.authoritiesByUsernameQuery(userAuthorities)
		.dataSource(dataSource)
		.passwordEncoder(BeanUtil.getBeanFromContext(BCryptPasswordEncoder.class))
		.and()
		.userDetailsService(userDetalsService);
	}
}
