package de.ffm.rka.rkareddit.security.mock;

import java.util.Arrays;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.repository.UserRepository;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * creates userDetailsService bean for testing
 * @author kaproma
 *
 */
@TestConfiguration
public class SpringSecurityTestConfig   {
	
	@Autowired
	private UserRepository userRepository;

	@SuppressWarnings("unchecked")
	private Collection<UserDetails> users = Collections.emptyList();
	
    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
    	
    	User masterUser = userRepository.findByEmailWithRoles("romakapt@gmx.de");
    	User user = userRepository.findByEmailWithRoles("dascha@gmx.de");
        users = Arrays.asList(masterUser, user);
    	InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager(users);
       	return inMemoryUserDetailsManager;
    }

	public Collection<UserDetails> getUsers() {
		return users;
	}
    
    
}
