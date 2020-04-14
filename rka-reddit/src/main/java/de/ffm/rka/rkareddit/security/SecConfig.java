package de.ffm.rka.rkareddit.security;

import static de.ffm.rka.rkareddit.security.Role.*;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import de.ffm.rka.rkareddit.util.BeanUtil;

/**
 * This is a configuration service for authentication
 * secures role-based requests and url-based requests
 * @author rka
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecConfig extends WebSecurityConfigurerAdapter {
		
	@Bean
    public AuthenticationSuccessHandler userSuccessfullAthenticationHandler(){
        return new UserSuccessfullAthenticationHandler();
    }
	
	@Bean
    public AuthenticationFailureHandler userFailureAthenticationHandler(){
        return new UserFailureAuthenticationHandler();
    }
	
	@Bean
    public UserDetailsService getUserDetailsService(){
        return new UserDetailsServiceImpl();
    }
		
	@Override
	public void configure(HttpSecurity http) throws Exception {
		final int oneDay = 86400;
		http.csrf().disable()
 					.headers().frameOptions().disable()
 			.and()

 			.authorizeRequests()			
							.antMatchers("/","/resources/**").permitAll()
							.antMatchers("/links/").permitAll()	
							.antMatchers("/login*","/profile/public","/invalidSession*", "/sessionExpired*").permitAll()
							.antMatchers("/profile/private","/links/link/create**").authenticated()
							.antMatchers("/data/h2-console/**").hasRole(DBA.name())
							.requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole(ACTUATOR.name())
			.and()
			.formLogin().loginPage("/login")
						.usernameParameter("email")
						.successHandler(userSuccessfullAthenticationHandler())
						.failureHandler(userFailureAthenticationHandler())
						.failureUrl("/login?error")
			.and()
		    .exceptionHandling().accessDeniedPage("/links/")
		    .and()
			.logout().invalidateHttpSession(true)
					.clearAuthentication(true)
			.and()
			.rememberMe().key("uniqueAndSecret")
						 .tokenValiditySeconds(oneDay)
			.and()
			.sessionManagement()
						.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)						
						.maximumSessions(1)
						.expiredUrl("/login?oneSession");	
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(BeanUtil.getBeanFromContext(BCryptPasswordEncoder.class));
		provider.setUserDetailsService(getUserDetailsService());
		return provider;
	}
}
