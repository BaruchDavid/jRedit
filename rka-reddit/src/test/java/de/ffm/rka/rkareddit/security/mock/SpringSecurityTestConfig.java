package de.ffm.rka.rkareddit.security.mock;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.repository.UserRepository;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * creates userDetailsService bean for testing
 * @author kaproma
 *
 */
@TestConfiguration
public class SpringSecurityTestConfig   {
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@SuppressWarnings("unchecked")
	private Collection<UserDetails> users = Collections.emptyList();
	
    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
    	
    	User masterUser = userRepository.findByEmailWithRoles("romakapt@gmx.de")
										.orElseThrow(() ->UserDetailsServiceImpl.throwUserNameNotFoundException("romakapt@gmx.de"));
    	User user = userRepository.findByEmailWithRoles("dascha@gmx.de")
										.orElseThrow(() ->UserDetailsServiceImpl.throwUserNameNotFoundException("romakapt@gmx.de"));
        users = Arrays.asList(masterUser, user);
    	return userDetailsService;
    }

	public Collection<UserDetails> getUsers() {
		return users;
	}
}
