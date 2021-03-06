package de.ffm.rka.rkareddit.service;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.repository.UserRepository;
import de.ffm.rka.rkareddit.util.BeanUtil;

/**
 * manages login- and register-process
 * @author RKA
 *
 */
@Service
@Transactional(readOnly = true)
public class UserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
	private UserRepository userRepository;
	private RoleService roleService;
	private final MailService mailService;

	
	public UserService(MailService mailService,UserRepository userRepository, RoleService roleService) {

		this.userRepository = userRepository;
		this.roleService= roleService;
		this.mailService  = mailService;
	}
	
	/**
	 * decodes pw
	 * assign role
	 * set activation code
	 * disable user before saving
	 * send activation email
	 * @author RKA
	 * @return user
	 */
	public User register(User user) {
		BCryptPasswordEncoder encoder = BeanUtil.getBeanFromContext(BCryptPasswordEncoder.class);
		String secret = encoder.encode(user.getPassword());
		user.setPassword(secret);
		user.setConfirmPassword(secret);
		user.addRole(roleService.findByName("ROLE_USER"));
		user.setActivationCode(UUID.randomUUID().toString());
		userRepository.saveAndFlush(user);
		sendActivatonEmail(user);
		return user;
	}
	
	/**
	 * fetch User with retrieved List<Link> with mail
	 * @param userId
	 */
	public User getUserWithLinks(String userId){
		return userRepository.fetchUserWithLinks(userId);
	}
	
	/**
	 * find user for activation
	 */
	public Optional<User> findUserByMailAndActivationCode(String mail, String code){
		LOGGER.info("FIND USER BY MAIL {} AND ACTIVATION_CODE {}", mail, code);
		return userRepository.findByEmailAndActivationCode(mail, code);
	}
	
	private void sendActivatonEmail(User user) {
		mailService.sendActivationEmail(user);
	}
	
	public void sendWelcomeEmail(User user) {
		mailService.sendWelcomeEmail(user);
	}
	
	@Transactional(readOnly = false)
	public User save(User user) {
		User newUser = userRepository.save(user);
		LOGGER.info("new User has been saved {}", newUser.toString());
		return newUser;
	}
	
	@Transactional(readOnly = false)
	public void saveUsers(User...users ) {
		Arrays.asList(users).stream()
							.forEach(user -> {
								LOGGER.info("TRY TO SAVE USER {}", user.getEmail());
								User userSaved = userRepository.saveAndFlush(user);
								LOGGER.info("USER SAVED WITH ID {} AND USERNAME {} ", userSaved.getUserId(),userSaved.getEmail());
							});
	}
		
	/**
	 * find somebody by username
	 * @param username from searched user
	 */
	@Cacheable("userInfo")
	public Optional<User> findUserById(String username){
		LOGGER.info("TRY TO FIND SER BY USERNAME {}", username);
		Optional<User> existsUser = userRepository.findByEmail(username);
		existsUser.ifPresent(user -> LOGGER.info("USER FOUND WITH ID {}", user.getUserId()));
		return existsUser;
	}

	/**
	 * fetch User with retrieved List<Comment> with mail
	 * @param userId
	 */
	public User getUserWithComments(String userId) {
		
		return userRepository.fetchUserWithComments(userId);
	}
}
