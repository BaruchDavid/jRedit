package de.ffm.rka.rkareddit.service;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.repository.UserRepository;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;
import de.ffm.rka.rkareddit.util.BeanUtil;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
	private UserDetailsServiceImpl userDetailsService;
	
	public UserService(MailService mailService, UserRepository userRepository, 
						RoleService roleService, ModelMapper modelMapper, UserDetailsServiceImpl userDetailsService) {
		this.userDetailsService = userDetailsService;
		this.userRepository = userRepository;
		this.roleService= roleService;
		this.mailService  = mailService;
		this.modelMapper = modelMapper;
	}
	
	/**
	 * decodes pw assign role set activation code
	 * disable user before saving , send activation email reregister user
	 * @return UserDTO
	 * @throws ServiceException 
	 */
	@Transactional(readOnly = false)
	public UserDTO register(UserDTO userDto) throws ServiceException {
		userDto.setActivationCode(String.valueOf(UUID.randomUUID()));
		User newUser = modelMapper.map(userDto, User.class);
		String secret = "";
		BCryptPasswordEncoder encoder = BeanUtil.getBeanFromContext(BCryptPasswordEncoder.class);
		secret = encoder.encode(newUser.getPassword());
		newUser.setPassword(secret);
		newUser.setConfirmPassword(secret);
		newUser.addRole(roleService.findByName("ROLE_USER"));
		sendActivatonEmail(userDto);
		return modelMapper.map(userRepository.saveAndFlush(newUser), UserDTO.class);
	}
	
	@Transactional(readOnly = false)
	public UserDTO emailChange(UserDTO userDto) throws ServiceException {
		userDto.setActivationCode(String.valueOf(UUID.randomUUID()));
		User newUser = getUser(userDto.getEmail());
		userDto.setEmail(userDto.getNewEmail());
		newUser.setNewEmail(userDto.getNewEmail());
		newUser.setActivationCode(userDto.getActivationCode());
		LOGGER.info("User changes Email: OLD {} NEW {}", newUser.getEmail(), newUser.getNewEmail());
		sendEmailToNewUserEmailAddress(userDto);
		return modelMapper.map(userRepository.saveAndFlush(newUser), UserDTO.class);
	}
	
	@Transactional(readOnly = false)
	public Optional<UserDTO> emailActivation(final String email, final String activationCode, final boolean isNewEmail) throws ServiceException {
		Optional<User> user = Optional.empty();
		Optional<UserDTO> userDTO = Optional.empty();
		if(isNewEmail) {
			user = findUserByMailAndReActivationCode(email, activationCode);
		} else {
			user = findUserByMailAndActivationCode(email, activationCode);
		}
		if(user.isPresent()) {			
			User newUser = user.get();
			newUser.setEnabled(true);
			newUser.setConfirmPassword(newUser.getPassword());
			newUser.setEmail(isNewEmail ? newUser.getNewEmail() : newUser.getEmail());
			newUser.setNewEmail(StringUtils.EMPTY);
			newUser.setActivationCode(StringUtils.EMPTY);
			save(newUser);
			userDetailsService.reloadUserAuthetication(email);
			userDTO = Optional.of(userDetailsService.mapUserToUserDto(user.get().getUsername()));
			sendWelcomeEmail(userDTO.get());
		}
		return userDTO;
	}
	
	
	
	private void sendEmailToNewUserEmailAddress(UserDTO userDto) throws ServiceException {
		mailService.sendEmailToNewEmailAccount(userDto);
		
	}

	/**
	 * changes user details
	 * @param userDto
	 */
	@Transactional(readOnly = false)
	public void changeUserDetails(UserDTO userDto) {
		
		User user = getUser(userDto.getEmail());
		user.setFirstName(userDto.getFirstName());
		user.setSecondName(userDto.getSecondName());
		user.setAliasName(userDto.getAliasName());
		userRepository.saveAndFlush(user);
	}

	private User getUser(String userMail) {
		User user = userRepository.findByEmail(userMail)
							.orElseThrow(() -> { 
										LOGGER.warn("{} Could not be found to be changed", userMail);
										return new  UsernameNotFoundException(userMail);
										});
		return user;
	}
	
	/**
	 * change user Password
	 * @param userDto
	 */
	@Transactional(readOnly = false)
	public void changeUserPassword(UserDTO userDto) {
		
		User user = getUser(userDto.getEmail());
		BCryptPasswordEncoder encoder = BeanUtil.getBeanFromContext(BCryptPasswordEncoder.class);
		String secret = encoder.encode(userDto.getNewPassword());
		user.setPassword(secret);
		user.setConfirmPassword(secret);
		userRepository.saveAndFlush(user);
	}
	public UserDTO updateUser(UserDTO userDto) {
		User newUser = modelMapper.map(userDto, User.class);
		userRepository.saveAndFlush(newUser);
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
	
	private void sendActivatonEmail(UserDTO user) throws ServiceException {
		mailService.sendActivationEmail(user);
	}
	
	public void sendWelcomeEmail(UserDTO user) throws ServiceException {
		mailService.sendWelcomeEmail(user);
	}
	
	@Transactional(readOnly = false)
	public User save(User user) {
		User newUser = userRepository.save(user);
		LOGGER.info("new User has been saved {}", newUser);
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
		return Optional.of(getUser(username));
	}

	/**
	 * fetch User with retrieved List<Comment> with mail
	 * @param userId
	 */
	public User getUserWithComments(String userId) {
		
		return userRepository.fetchUserWithComments(userId);
	}

	public boolean lockUser(String userName) throws ServiceException {
		Optional<User> dbUser = Optional.of(getUser(userName));
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
		return userRepository.findAll();
	}

	public Optional<User> findUserByMailAndReActivationCode(String email, String activationCode) {
		return userRepository.findByNewEmailAndActivationCode(email, activationCode);
	}


    public User getUserClickedLinks(String requestedUser) {
		return userRepository.findClickedUserLinks(requestedUser);
    }
}
