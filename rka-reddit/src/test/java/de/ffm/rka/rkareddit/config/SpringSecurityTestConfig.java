package de.ffm.rka.rkareddit.config;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.repository.UserRepository;
import de.ffm.rka.rkareddit.service.UserDetailsServiceImpl;
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
 *
 * @author kaproma
 */
@TestConfiguration
public class SpringSecurityTestConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @SuppressWarnings("unchecked")
    private Collection<UserDetails> users = Collections.emptyList();

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        //collectUsersFromDatabase();
        return userDetailsService;
    }

    private void collectUsersFromDatabase() {
        User masterUser = userRepository.findByEmailWithRoles("kaproma@yahoo.de")
                .orElse(new User("kaproma@yahoo.de", "$2a$10$kd02GLWJlGR94dyTT7xiLu07CejGocL0oqSSRsInjvwahu3d900ne"));
        //.orElseThrow(() ->UserDetailsServiceImpl.throwUserNameNotFoundException("kaproma@yahoo.de"));
        User dascha = userRepository.findByEmailWithRoles("dascha@gmx.de")
                .orElse(new User("dascha@gmx.de", "$2a$10$kd02GLWJlGR94dyTT7xiLu07CejGocL0oqSSRsInjvwahu3d900ne"));
        //.orElseThrow(() ->UserDetailsServiceImpl.throwUserNameNotFoundException("dascha@gmx.de"));
        User grom = userRepository.findByEmailWithRoles("grom@gmx.de")
                .orElse(new User("grom@gmx.de", "$2a$10$kd02GLWJlGR94dyTT7xiLu07CejGocL0oqSSRsInjvwahu3d900ne"));
        //.orElseThrow(() ->UserDetailsServiceImpl.throwUserNameNotFoundException("grom@gmx.de"));
        users = Arrays.asList(masterUser, dascha);
    }

    public Collection<UserDetails> getUsers() {
        return users;
    }
}
