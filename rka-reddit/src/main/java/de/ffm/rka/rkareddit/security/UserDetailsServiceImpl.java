package de.ffm.rka.rkareddit.security;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.repository.UserRepository;

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
															LOGGER.warn("{} Could not be found", username);
															return new  UsernameNotFoundException(username); 
															});
	}
	
	public UserDTO mapUserToUserDto(String usrName) {
		User usrObj = Optional.ofNullable((User) loadUserByUsername(usrName))
								.orElseThrow(()-> new UsernameNotFoundException("user not found"));
		return modelMapper.map(usrObj, UserDTO.class);
	}

}
