package de.ffm.rka.rkareddit.security.mock;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import de.ffm.rka.rkareddit.domain.Role;
import de.ffm.rka.rkareddit.domain.User;

/**
 * creates userDetailsService bean for testing
 * @author kaproma
 *
 */
@TestConfiguration

public class SpringSecurityTestConfig {

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
    	Set<Role> masterRoles = new HashSet<>();
    	masterRoles.add(new Role("ROLE_USER"));
    	masterRoles.add(new Role("ROLE_DBA"));
    	masterRoles.add(new Role("ROLE_ADMIN"));
    	masterRoles.add(new Role("ROLE_ACTUATOR"));
    	
    	User masterUser = new User("romakapt@gmx.de", 
    								"$2a$10$kd02GLWJlGR94dyTT7xiLu07CejGocL0oqSSRsInjvwahu3d900ne",
    								masterRoles);
    	
    	Set<Role> userRoles = new HashSet<>();
    	userRoles.add(new Role("ROLE_USER"));
      	
    	User basicUser = new User("dascha@gmx.de", 
					    			"$2a$10$huJEV8HA6ty9BzNlRqHyG.QMPE//p4lyMyfcTqnSpTe7fxlxybs2e",
					    			userRoles);

        
    	InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager(Arrays.asList(
        		masterUser, basicUser
        ));
    	
    	
    	return inMemoryUserDetailsManager;
    }

}
