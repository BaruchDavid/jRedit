package de.ffm.rka.rkareddit.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
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
	private ModelMapper modelMapper;
	
	public UserService(MailService mailService,UserRepository userRepository, RoleService roleService, ModelMapper modelMapper) {

		this.userRepository = userRepository;
		this.roleService= roleService;
		this.mailService  = mailService;
		this.modelMapper = modelMapper;
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
	public UserDTO register(UserDTO userDto) {
		User newUser = modelMapper.map(userDto, User.class);
		BCryptPasswordEncoder encoder = BeanUtil.getBeanFromContext(BCryptPasswordEncoder.class);
		String secret = encoder.encode(newUser.getPassword());
		newUser.setPassword(secret);
		newUser.setConfirmPassword(secret);
		newUser.addRole(roleService.findByName("ROLE_USER"));
		newUser.setActivationCode(UUID.randomUUID().toString());
		userRepository.saveAndFlush(newUser);
		sendActivatonEmail(userDto);
		return userDto;
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
	
	private void sendActivatonEmail(UserDTO user) {
		mailService.sendActivationEmail(user);
	}
	
	public void sendWelcomeEmail(UserDTO user) {
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

	public boolean lockUser(String userName) {
		Optional<User> dbUser = userRepository.findByEmail(userName);
		boolean locked = false;
		if(dbUser.isPresent()) {
			dbUser.get().setEnabled(false);
			mailService.sendEmail(userName, "Account is locked", "account is locked", false, false);
			locked = true;
		} else {
			//delete all links with this tag
			//delete tag
		}
		return locked;
	}

	public List<User> findAll() {
		// TODO Auto-generated method stub
		return userRepository.findAll();
	}
}
