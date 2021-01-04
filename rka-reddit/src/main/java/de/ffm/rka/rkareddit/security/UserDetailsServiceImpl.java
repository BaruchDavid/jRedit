package de.ffm.rka.rkareddit.security;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    /*TODO: Caching einführen*/
    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByEmailWithRoles(username)
                .orElseThrow(() -> throwUserNameNotFoundException(username));
    }

    public void reloadUserAuthentication(final String newEmail) {
        Authentication oldAuth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmailWithRoles(newEmail)
                .orElseThrow(() -> {
                    LOGGER.error("USER {} could not be found", newEmail);
                    return new UsernameNotFoundException(newEmail);
                });
        Authentication newAuth = new UsernamePasswordAuthenticationToken(user, oldAuth.getCredentials(), oldAuth.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    public static UsernameNotFoundException throwUserNameNotFoundException(String username) {
        LOGGER.error("USER {} could not be found", username);
        Supplier<UsernameNotFoundException> supplier = () -> new UsernameNotFoundException(username);
        return supplier.get();
    }

    public static AuthenticationException throwUnauthenticatedUserException(String request) {
        LOGGER.error("could not be authenticated on request {}", request);
        Supplier<AuthenticationException> supplier = () -> new PreAuthenticatedCredentialsNotFoundException(request);
        return supplier.get();

    }
}
