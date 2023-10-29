package de.ffm.rka.rkareddit.security;

import de.ffm.rka.rkareddit.controller.logout.LogoutHandlerImpl;
import de.ffm.rka.rkareddit.exception.GlobalAccessDeniedHandler;
import de.ffm.rka.rkareddit.service.UserDetailsServiceImpl;
import de.ffm.rka.rkareddit.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static de.ffm.rka.rkareddit.security.Role.*;

/**
 * This is a configuration service for authentication
 * secures role-based requests and url-based requests
 *
 * @author rka
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecConfig {

    @Autowired
    CacheManager cacheManager;

    @Bean
    public AuthenticationSuccessHandler userSuccessfulAuthenticationHandler() {
        return new UserSuccessfullAthenticationHandler();
    }

    @Bean
    public AuthenticationFailureHandler userFailureAuthenticationHandler() {
        return new UserFailureAuthenticationHandler();
    }

    @Bean
    public UserDetailsService getUserDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public AccessDeniedHandler getAccessDeniedHandler() {
        return new GlobalAccessDeniedHandler();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        final int oneDay = 86400;
        http.csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeHttpRequests(requests -> {
                    try {
                        requests
                                .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/links/")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/resources/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/profile/public")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/login*")).hasRole(ANONYMOUS.name())
                                .requestMatchers(new AntPathRequestMatcher("/links/link/{signature}", HttpMethod.GET.toString())).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/profile/private", HttpMethod.GET.toString())).hasRole(USER.name())
                                .requestMatchers(new AntPathRequestMatcher("/profile/private/me", HttpMethod.GET.toString())).hasRole(USER.name())
                                .requestMatchers(new AntPathRequestMatcher("/profile/private/me/password", HttpMethod.GET.toString())).hasRole(USER.name())
                                .requestMatchers(new AntPathRequestMatcher("/profile/private/me/update/email", HttpMethod.GET.toString())).hasRole(USER.name())
                                .requestMatchers(new AntPathRequestMatcher("/profile/private/me/update/email", HttpMethod.PATCH.toString())).hasRole(USER.name())
                                .requestMatchers(new AntPathRequestMatcher("/profile/private/me/update", HttpMethod.PUT.toString())).hasRole(USER.name())
                                .requestMatchers(new AntPathRequestMatcher("/profile/private/me/password", HttpMethod.PUT.toString())).hasRole(USER.name())
                                .requestMatchers(new AntPathRequestMatcher("/link/{lSig}/vote/direction/{direction}/votecount/{voteCount}", HttpMethod.GET.toString())).hasRole(USER.name())
                                .requestMatchers(new AntPathRequestMatcher("/tag/deleteTag/{tagId}", HttpMethod.GET.toString())).hasRole(ADMIN.name())
                                .requestMatchers(new AntPathRequestMatcher("/links/link", HttpMethod.POST.toString())).hasRole(ADMIN.name())
                                .requestMatchers(new AntPathRequestMatcher("/comments/comment", HttpMethod.POST.toString())).hasRole(USER.name())
                                .requestMatchers(new AntPathRequestMatcher("/data/h2-console/**")).hasRole(DBA.name())
                                .and()
                                .requiresChannel(requestRegistry -> requestRegistry.anyRequest().requiresSecure())
                                .authorizeRequests(interceptUrlRegistry -> interceptUrlRegistry.anyRequest().permitAll())
                                .formLogin().loginPage("/login")
                                .usernameParameter("email")
                                .successHandler(userSuccessfulAuthenticationHandler())
                                .failureHandler(userFailureAuthenticationHandler())
                                .failureUrl("/login?error")
                                .and()
                                .exceptionHandling().accessDeniedHandler(getAccessDeniedHandler())
                                .and()
                                .logout().deleteCookies("JSESSIONID")
                                .logoutUrl("/logout")
                                .addLogoutHandler(new LogoutHandlerImpl(cacheManager))
                                .invalidateHttpSession(true)
                                .clearAuthentication(true)
                                .and()
                                .rememberMe().key("uniqueAndSecret")
                                .tokenValiditySeconds(oneDay)
                                .and()
                                .sessionManagement()
                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                                .maximumSessions(1)
                                .expiredUrl("/login?oneSession");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        return http.build();
    }


    /*@Override
    public void configure(HttpSecurity http) throws Exception {
        final int oneDay = 86400;
        http.csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .antMatchers("/", "/links/", "/resources/**").permitAll()
                .antMatchers("/profile/public").permitAll()
                .antMatchers(HttpMethod.GET, "/links/link/{signature}").permitAll()
                .antMatchers("/login*").hasRole(ANONYMOUS.name())
                .antMatchers("/registration").hasRole(ANONYMOUS.name())
                .antMatchers(HttpMethod.GET, "/profile/private").hasRole(USER.name())
                .antMatchers(HttpMethod.GET, "/profile/private/me").hasRole(USER.name())
                .antMatchers(HttpMethod.GET, "/profile/private/me/password").hasRole(USER.name())
                .antMatchers(HttpMethod.GET, "/profile/private/me/update/email").hasRole(USER.name())
                .antMatchers(HttpMethod.GET, "/links/link").hasRole(USER.name())
                .antMatchers(HttpMethod.PATCH, "/profile/private/me/update/email").hasRole(USER.name())
                .antMatchers(HttpMethod.PUT, "/profile/private/me/update").hasRole(USER.name())
                .antMatchers(HttpMethod.PUT, "/profile/private/me/password").hasRole(USER.name())
                .antMatchers("/link/{lSig}/vote/direction/{direction}/votecount/{voteCount}").hasRole(USER.name())
                .antMatchers(HttpMethod.DELETE, "/tag/deleteTag/{tagId}").hasRole(ADMIN.name())
                .antMatchers(HttpMethod.POST, "/tags/tag/create").hasRole(ADMIN.name())
                .antMatchers(HttpMethod.POST, "/links/link").hasRole(USER.name())
                .antMatchers(HttpMethod.POST, "/comments/comment").hasRole(USER.name())
                .antMatchers("/data/h2-console/**").hasRole(DBA.name())
                //.requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole(ACTUATOR.name())
                .and()
                .requiresChannel(requestRegistry -> requestRegistry.anyRequest().requiresSecure())
                .authorizeRequests(interceptUrlRegistry -> interceptUrlRegistry.anyRequest().permitAll())
                .formLogin().loginPage("/login")
                .usernameParameter("email")
                .successHandler(userSuccessfulAuthenticationHandler())
                .failureHandler(userFailureAuthenticationHandler())
                .failureUrl("/login?error")
                .and()
                .exceptionHandling().accessDeniedHandler(getAccessDeniedHandler())
                .and()
                .logout().deleteCookies("JSESSIONID")
                .logoutUrl("/logout")
                .addLogoutHandler(new LogoutHandlerImpl(cacheManager))
                .invalidateHttpSession(true)
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
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider()).eraseCredentials(false);
    }*/

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(BeanUtil.getBeanFromContext(BCryptPasswordEncoder.class));
        provider.setUserDetailsService(getUserDetailsService());
        return provider;
    }
}
