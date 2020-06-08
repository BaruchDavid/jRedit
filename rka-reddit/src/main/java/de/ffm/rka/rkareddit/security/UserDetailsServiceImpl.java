package de.ffm.rka.rkareddit.security;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ModelMapper modelMapper;

	@Override
	public UserDetails loadUserByUsername(String username) {
		return Optional.ofNullable(userRepository.findByEmailWithRoles(username))
										.orElseThrow(() -> { 
															LOGGER.error("USER {} could not be found", username);
															return new  UsernameNotFoundException(username); 
															});
	}
	
	public UserDTO mapUserToUserDto(String usrName) {
		return modelMapper.map((User)loadUserByUsername(usrName), UserDTO.class);
	}

	public void reloadUserAuthetication(final String newEmail) {
		Authentication oldAuth = SecurityContextHolder.getContext().getAuthentication();
		Authentication newAuth = SecurityContextHolder.createEmptyContext().getAuthentication();
		User user = userRepository.findByEmailWithRoles(newEmail);
		newAuth = new UsernamePasswordAuthenticationToken(user, oldAuth.getCredentials(), oldAuth.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(newAuth);
	}
	
}
