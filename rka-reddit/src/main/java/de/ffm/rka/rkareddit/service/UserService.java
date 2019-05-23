package de.ffm.rka.rkareddit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.repository.UserRepository;


@Service
public class UserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
	private UserRepository userRepository;
	
	public UserService(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}
	
	public User register(User user) {
		return user;
	}
	
	public User save(User user) {
		User newUser = userRepository.save(user);
		LOGGER.info("new User has been saved {}", newUser.toString());
		return newUser;
	}
			
	
}
