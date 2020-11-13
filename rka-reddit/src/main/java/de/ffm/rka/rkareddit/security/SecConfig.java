package de.ffm.rka.rkareddit.security;

import de.ffm.rka.rkareddit.exception.GlobalAccessDeniedHandler;
import de.ffm.rka.rkareddit.util.BeanUtil;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import static de.ffm.rka.rkareddit.security.Role.*;

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
    public AuthenticationSuccessHandler userSuccessfulAuthenticationHandler(){
        return new UserSuccessfullAthenticationHandler();
    }
	
	@Bean
    public AuthenticationFailureHandler userFailureAuthenticationHandler(){
        return new UserFailureAuthenticationHandler();
    }
	
	@Bean
    public UserDetailsService getUserDetailsService(){
        return new UserDetailsServiceImpl();
    }
		
	@Bean
	public AccessDeniedHandler getAccessDeniedHandler() {
		return new GlobalAccessDeniedHandler();
	}
	
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		final int oneDay = 86400;
		http.csrf().disable()
 					.headers().frameOptions().disable()
 			.and()
 			.authorizeRequests()			
							.antMatchers("/","/links/","/resources/**").permitAll()
							.antMatchers("/login*").hasRole(ANONYMOUS.name())
							.antMatchers("/profile/public").permitAll()
							.antMatchers("/registration").hasRole(ANONYMOUS.name())
							.antMatchers(HttpMethod.GET, "/profile/private").hasRole(USER.name())
							.antMatchers(HttpMethod.GET, "/profile/private/me/{email:.+}").hasRole(USER.name())
							.antMatchers(HttpMethod.GET, "/profile/private/me/{email:.+}/password").hasRole(USER.name())
							.antMatchers(HttpMethod.GET, "/profile/private/me/update/email/{email:.+}").hasRole(USER.name())
							.antMatchers(HttpMethod.PATCH, "/profile/private/me/update/email/{email:.+}").hasRole(USER.name())
							.antMatchers(HttpMethod.PUT, "/profile/private/me/update").hasRole(USER.name())
							.antMatchers(HttpMethod.PUT, "/profile/private/me/{email:.+}/password").hasRole(USER.name())
							.antMatchers("/vote/link/{linkId}/direction/{direction}/votecount/{voteCount}").hasRole(USER.name())
							.antMatchers(HttpMethod.POST, "/tags/tag/create", "/tag/deleteTag/{tagId}").hasRole(ADMIN.name())
							.antMatchers(HttpMethod.DELETE, "/tag/deleteTag/{tagId}").hasRole(ADMIN.name())
							.antMatchers(HttpMethod.GET, "/links/link").hasRole(USER.name())
							.antMatchers(HttpMethod.POST, "/links/link").hasRole(USER.name())
							.antMatchers(HttpMethod.POST, "/links/link/comments").hasRole(USER.name())
							.antMatchers("/data/h2-console/**").hasRole(DBA.name())
							.requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole(ACTUATOR.name())
							
			.and()
			.formLogin().loginPage("/login")
						.usernameParameter("email")
						.successHandler(userSuccessfulAuthenticationHandler())
						.failureHandler(userFailureAuthenticationHandler())
						.failureUrl("/login?error")
			.and()
		    .exceptionHandling().accessDeniedPage("/links/")
		    .and()
			.logout().deleteCookies("JSESSIONID")
					.logoutUrl("/logout")
					.invalidateHttpSession(true)
					.clearAuthentication(true)
			.and()
 			.exceptionHandling().accessDeniedHandler(getAccessDeniedHandler())		
			.and()
			.rememberMe().key("uniqueAndSecret")
						 .tokenValiditySeconds(oneDay)
			.and()
			.sessionManagement()
						.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)						
						.maximumSessions(1)
						.expiredUrl("/login?oneSession")
			;	
	}


	@Override
	protected void configure(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(authenticationProvider()).eraseCredentials(false);
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(BeanUtil.getBeanFromContext(BCryptPasswordEncoder.class));
		provider.setUserDetailsService(getUserDetailsService());
		return provider;
	}
}
