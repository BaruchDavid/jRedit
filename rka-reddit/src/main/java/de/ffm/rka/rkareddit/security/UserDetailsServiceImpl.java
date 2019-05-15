package de.ffm.rka.rkareddit.security;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.repository.UserRepository;
import de.ffm.rka.rkareddit.util.BeanUtil;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
	
	@Autowired
	private UserRepository userRepository;
	

	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		BCryptPasswordEncoder encoder = BeanUtil.getBeanFromContext(BCryptPasswordEncoder.class);
		LOGGER.info("ps von roman is {}", encoder.encode("roman"));
		Optional<User> user = userRepository.findByEmail(username);
		if(user.isPresent()) {
			return user.get();
		} else {
			throw new  UsernameNotFoundException(username);
		}
	}

}
