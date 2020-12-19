package de.ffm.rka.rkareddit.service;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.repository.UserRepository;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;
import de.ffm.rka.rkareddit.util.BeanUtil;
import de.ffm.rka.rkareddit.util.FileNIO;
import de.ffm.rka.rkareddit.util.ImageManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * manages login- and register-process
 * @author RKA
 *
 */
@Service
@Transactional(readOnly = true)
public class UserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
	private static final int TARGET_WITDH = 300;
	private final UserRepository userRepository;
	private final RoleService roleService;
	private final MailService mailService;
	private final UserDetailsServiceImpl userDetailsService;
	private final LinkService linkService;


	public UserService(MailService mailService, UserRepository userRepository,
					   RoleService roleService, UserDetailsServiceImpl userDetailsService, LinkService linkService) {
		this.userDetailsService = userDetailsService;
		this.userRepository = userRepository;
		this.roleService= roleService;
		this.mailService  = mailService;
		this.linkService = linkService;
	}

	/**
	 * retrieved link has due of lazy-initialization no comments
	 * here comments will be set
	 * @param userLinks with no comments
	 */
	private Set<Link> fillLinkWithSuitableComments(Set<Link> userLinks) {
		Set<Long> linkIds = linkService.getLinkIds(userLinks);
		return linkService.findLinksWithCommentsByLinkIds(linkIds)
				.orElseGet(Collections::emptySet);
	}

	/**
	 * decodes pw assign role set activation code
	 * disable user before saving , send activation email reregister user
	 * @return UserDTO
	 * @throws ServiceException
	 */
	@Transactional(readOnly = false)
	public UserDTO register(UserDTO userDto) throws ExecutionException, InterruptedException {
		userDto.setActivationCode(String.valueOf(UUID.randomUUID()));
		User newUser = UserDTO.mapUserDtoToUser(userDto);
		String secret;
		BCryptPasswordEncoder encoder = BeanUtil.getBeanFromContext(BCryptPasswordEncoder.class);
		secret = encoder.encode(newUser.getPassword());
		newUser.setPassword(secret);
		newUser.setConfirmPassword(secret);
		newUser.addRole(roleService.findByName("ROLE_USER"));
		Future<Boolean> sendResult = sendActivatonEmail(userDto);
		if(sendResult.isDone()){
			if(sendResult.get()){
				return UserDTO.mapUserToUserDto(userRepository.saveAndFlush(newUser));
			} else {
				LOGGER.info("REGISTRATION FAILED FOR USER {} and EMAIL {}", userDto.getEmail(), userDto.getEmail());
			}
		}
		return userDto;
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
		return UserDTO.mapUserToUserDto(userRepository.saveAndFlush(newUser));
	}

	@Transactional(readOnly = false)
	public Optional<UserDTO> emailActivation(final String email, final String activationCode, final boolean isNewEmail) throws ServiceException {
		Optional<User> user;
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
			userDetailsService.reloadUserAuthentication(email);
			userDTO = Optional.of(UserDTO.mapUserToUserDto(newUser));
			sendWelcomeEmail(userDTO.get());
		}
		return userDTO;
	}



	private void sendEmailToNewUserEmailAddress(UserDTO userDto) throws ServiceException {
		mailService.sendEmailToNewEmailAccount(userDto);

	}

	/**
	 * changes user details
	 * @param  userDto hold changes to be saved
	 */
	@Transactional(readOnly = false)
	public void changeUserDetails(UserDTO userDto) {

		User user = getUser(userDto.getEmail());
		user.setFirstName(userDto.getFirstName());
		user.setSecondName(userDto.getSecondName());
		user.setAliasName(userDto.getAliasName());
		userRepository.saveAndFlush(user);
	}

	// TODO: 29.11.2020 frage user über userDetailsService
	public User getUser(String userMail) {
		return userRepository.findByEmail(userMail)
							.orElseThrow(() -> {
										LOGGER.warn("{} Could not be found to be changed", userMail);
										return new  UsernameNotFoundException(userMail);
										});
	}

	@Transactional
	public Optional<byte[]> getUserPic(String userMail) {
		return Optional.of(getUser(userMail).getProfileFoto());
	}

	/**
	 * change user Password
	 * @param userDto hold changes to be saved
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
		User newUser = UserDTO.mapUserDtoToUser(userDto);
		userRepository.saveAndFlush(newUser);
		return userDto;

	}

	/**
	 * fetch User with retrieved List<Link> with mail
	 * @param userId is a user email
	 */
	public User getUserWithLinks(String userId){
//		User user =  userRepository.fetchUserWithLinks(userId);
		User user =  userRepository.fetchUserWithLinksAndComments(userId);
/*		List<Link> linksWithComments = new ArrayList<>(this.fillLinkWithSuitableComments(user.getUserLinks()));
		user.setUserLinks(linksWithComments);*/

		Set<Link> linksWithComments = new HashSet<>(this.fillLinkWithSuitableComments(user.getUserLinks()));
		user.setUserLinks(linksWithComments);
		return user;
	}

	/**
	 * find user for activation
	 */
	public Optional<User> findUserByMailAndActivationCode(String mail, String code){
		LOGGER.info("FIND USER BY MAIL {} AND ACTIVATION_CODE {}", mail, code);
		return userRepository.findByEmailAndActivationCode(mail, code);
	}

	private Future<Boolean> sendActivatonEmail(UserDTO user) {
		LOGGER.info("User-Service Thread: {}", Thread.currentThread().getName());
		return mailService.sendActivationEmail(user);
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
		Arrays.asList(users).forEach(user -> {
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
	 * @param userId is user email
	 */
	public User getUserWithComments(String userId) {
		return userRepository.fetchUserWithComments(userId);
	}

	public boolean lockUser(String userName) throws ServiceException {
		User dbUser = (User) userDetailsService.loadUserByUsername(userName);
		dbUser.setEnabled(false);
		mailService.sendEmail(userName, "Account is locked", "account is locked", false, false);
		return true;
	}

	public List<User> findAll() {
		return userRepository.findAll();
	}

	public Optional<User> findUserByMailAndReActivationCode(String email, String activationCode) {
		return userRepository.findByNewEmailAndActivationCode(email, activationCode);
	}

	/**
	 * retrieve list of previously clicked links
	 * @param requestedUser is a account owner, no public visitor
	 * @return either list of links oder empty list
	 */
	public Set<Link> findUserClickedLinks(final String requestedUser){
		Set<Link> links = Optional.ofNullable(userRepository.findClickedUserLinks(requestedUser))
						.map(User::getUserClickedLinks)
						.orElse(Collections.emptySet());
		links = new HashSet<>(this.fillLinkWithSuitableComments(new HashSet<>(links)));
		return links;
	}

	@Transactional(readOnly = false)
	public boolean saveNewUserPicture(InputStream inputStream, User user) throws IOException {
		byte[] pictureBytes = FileNIO.readStreamToByte(inputStream);
		if(pictureBytes.length != 0){
			user.setProfileFoto(pictureBytes);
			user.setFotoCreationDate(LocalDateTime.now());
			userRepository.save(user);
			LOGGER.info("new picture saved or user {}", user.getEmail());
			return true;
		} else {
			LOGGER.info("new picture could not saved or user {}", user.getEmail());
			return false;
		}
	}

	public InputStream resizeUserPic(InputStream inputStream, String extension) throws Exception {
		final BufferedImage bufferedImage = ImageManager.simpleResizeImage(inputStream, TARGET_WITDH);
		return new ByteArrayInputStream(FileNIO.readPictureToByteArray(bufferedImage, extension));
	}
}
