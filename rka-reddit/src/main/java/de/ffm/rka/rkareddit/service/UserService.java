package de.ffm.rka.rkareddit.service;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.exception.GlobalControllerAdvisor;
import de.ffm.rka.rkareddit.exception.RegisterException;
import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.repository.UserRepository;
import de.ffm.rka.rkareddit.util.BeanUtil;
import de.ffm.rka.rkareddit.util.FileNIO;
import de.ffm.rka.rkareddit.util.ImageManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;



/**
 * manages login- and register-process
 *
 * @author RKA
 */
@Service
@Transactional(readOnly = true)
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private static final int TARGET_WIDTH = 320;
    private static final String REGISTRATION_FAILED = "REGISTRATION FAILS ON SENDING EMAIL OR SAVING NEW USER: {}";
    private static final String REGISTRATION_ERROR = "REGISTRATION: {}";
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final MailService mailService;
    private final UserDetailsServiceImpl userDetailsService;
    private final LinkService linkService;

    @Value("${password.time.expiration}")
    private int maxTimeDiff;

    public UserService(MailService mailService, UserRepository userRepository,
                       RoleService roleService, UserDetailsServiceImpl userDetailsService, LinkService linkService) {
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.mailService = mailService;
        this.linkService = linkService;
    }

    /**
     * retrieved link has due of lazy-initialization no comments
     * here comments will be set
     *
     * @param userLinks with no comments
     */
    private Set<Link> fillLinkWithSuitableComments(Set<Link> userLinks) {
        Set<Long> linkIds = linkService.getLinkIds(userLinks);
        return linkService.findLinksWithCommentsByLinkIds(linkIds)
                .orElseGet(Collections::emptySet);
    }

    /**
     * decodes pw assign role set activation code
     * disable user before saving , send activation email register user
     *
     * @return UserDTO
     * @throws ServiceException if registration fails
     */
    @Transactional(readOnly = false)
    public void register(UserDTO newUserDto) throws ServiceException {
        newUserDto.setActivationCode(String.valueOf(UUID.randomUUID()));
        User newUser = UserDTO.mapUserDtoToUser(newUserDto);
        String secret = encodeUserPw(newUser.getPassword());
        newUser.setPassword(secret);
        newUser.setConfirmPassword(secret);
        newUser.addRole(roleService.findByName("ROLE_USER"));
        newUser.setActivationDeadLineDate(LocalDateTime.now().plusMinutes(5));
        LOGGER.info("User-Service Thread: {}", Thread.currentThread().getName());
        mailService.sendActivationEmail(newUserDto)
                .thenAccept((sent) -> saveNewUnregisteredUser(newUser))
                .exceptionally(ex -> {
                    LOGGER.error(REGISTRATION_FAILED, newUserDto.getEmail());
                    LOGGER.error(REGISTRATION_ERROR, ex);
                    return null;
                });

    }

    private String encodeUserPw(String password) {
        BCryptPasswordEncoder encoder = BeanUtil.getBeanFromContext(BCryptPasswordEncoder.class);
        return encoder.encode(password);
    }

    private void saveNewUnregisteredUser(User newUser) {
        if (isRegisteredEmailUnique(newUser.getEmail())){
            newUser = save(newUser);
            LOGGER.info("USER {} and EMAIL {} SUCCESSFULLY SAVED ON REGISTRATION", newUser.getEmail(), newUser.getEmail());
        } else {
            LOGGER.info("USER {} and EMAIL {} ARE ALREADY REGISTERED", newUser.getEmail(), newUser.getEmail());
        }
    }

    private boolean isRegisteredEmailUnique(String email) {
        return !userRepository.findByEmail(email).isPresent();
    }

    @Transactional(readOnly = false)
    public UserDTO emailChange(UserDTO userDto) throws ServiceException {
        User newUser = null;
        userDto.setActivationCode(String.valueOf(UUID.randomUUID()));
        newUser = getUser(userDto.getEmail());
        userDto.setEmail(userDto.getNewEmail());
        newUser.setNewEmail(userDto.getNewEmail());
        newUser.setActivationCode(userDto.getActivationCode());
        newUser.setActivationDeadLineDate(LocalDateTime.now());
        LOGGER.info("User changes Email: OLD {} NEW {}", newUser.getEmail(), newUser.getNewEmail());
        sendEmailToNewUserEmailAddress(userDto);
        return UserDTO.mapUserToUserDto(userRepository.saveAndFlush(newUser));
    }

    // TODO: 14.04.2022 wenn man sich normal registriert, soll nicht ein userDto des neu registrierten users zurückgegebe werden 
    @Transactional(readOnly = false)
    public UserDTO emailActivation(final String email, final String activationCode, final boolean isNewEmail)
            throws ServiceException, RegisterException {
        Optional<UserDTO> userDTO = Optional.empty();
        final Optional<User> userForMailActivation = findUserForMailActivation(email, activationCode, isNewEmail);
        if (userForMailActivation.isPresent()) {
            final boolean behindDeadline = TimeService.isBehindDeadline(maxTimeDiff,
                    userForMailActivation.get().getActivationDeadLineDate());
            if (!behindDeadline) {
                User newUser = userForMailActivation.get();
                newUser.setEnabled(true);
                newUser.setConfirmPassword(newUser.getPassword());
                newUser.setEmail(isNewEmail ? newUser.getNewEmail() : newUser.getEmail());
                newUser.setNewEmail(StringUtils.EMPTY);
                newUser.setActivationCode(StringUtils.EMPTY);
                newUser.setActivationDeadLineDate(LocalDateTime.of(5000, 1, 1, 0, 0));
                save(newUser);
                userDetailsService.reloadUserCredentials(email);
                userDTO = Optional.of(UserDTO.mapUserToUserDto(newUser));
                sendWelcomeEmail(userDTO.get());
            } else {
                throw GlobalControllerAdvisor.createServiceException("Der Aktivierungslink für die Email ist abgelaufen");
            }
        } else {
            LOGGER.error("NO USER HAS BEEN FOUND ON EMAIL {} AND ACTIVATION CODE {}", email, activationCode);
        }

        return userDTO.orElseThrow(() -> GlobalControllerAdvisor.createRegisterException(
                String.format("USER %s WITH ACTIVATION-CODE %s HAS BEEN NOT ACTIVATED SUCCESSFULLY", email, activationCode)));
    }

    private Optional<User> findUserForMailActivation(String email, String activationCode, boolean isNewEmail) {
        Optional<User> user;
        if (isNewEmail) {
            user = findUserByMailAndReActivationCode(email, activationCode);
        } else {
            user = findUserByMailAndActivationCode(email, activationCode);
        }
        return user;
    }


    public Optional<UserDTO> getUserForPasswordReset(final String email, final String activationCode) throws ServiceException {
        final String errorMessage = String.format("PASSWORD-RESETING: WRONG ACTIVATION-CODE %s ON %s", activationCode, email);
        final User user = findUserByMailAndActivationCode(email, activationCode)
                .orElseThrow(() -> GlobalControllerAdvisor.createServiceException(errorMessage));
        return Optional.of(UserDTO.mapUserToUserDto(user));
    }


    private void sendEmailToNewUserEmailAddress(UserDTO userDto) throws ServiceException{
        mailService.sendEmailToNewEmailAccount(userDto);
    }

    /**
     * changes user details
     *
     * @param userDto hold changes to be saved
     */
    @Transactional(readOnly = false)
    public void changeUserDetails(UserDTO userDto) {

        User user = getUser(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setSecondName(userDto.getSecondName());
        user.setAliasName(userDto.getAliasName());
        userRepository.saveAndFlush(user);
        userDetailsService.reloadUserCredentials(user.getEmail());
    }

    public User getUser(String userMail) {
        return (User) userDetailsService.loadUserByUsername(userMail);
    }

    @Transactional
    public Optional<byte[]> getUserPic(String userMail) {
        return Optional.of(getUser(userMail).getProfileFoto());
    }

    /**
     * change user Password
     *
     * @param userDto hold changes to be saved
     */
    @Transactional(readOnly = false)
    public void changeUserPassword(UserDTO userDto) throws ServiceException {
        User user = getUser(userDto.getEmail());
        if (!isActivationDeadlineExpired(user.getActivationDeadLineDate())) {
            String secret = encodeUserPw(userDto.getNewPassword());
            user.setActivationDeadLineDate(LocalDateTime.of(5000, 1, 1, 0, 0));
            user.setActivationCode("activation");
            user.setPassword(secret);
            user.setConfirmPassword(secret);
            userRepository.saveAndFlush(user);
        } else {
            throw GlobalControllerAdvisor.createServiceException(String.format("TIME FOR PW-RECOVERY FOR USER %s IS EXPIRED", userDto.getEmail()));
        }
    }

    public boolean isActivationDeadlineExpired(LocalDateTime activationDeadLineDate) {
        return TimeService.isBehindDeadline(maxTimeDiff, activationDeadLineDate);
    }

    public UserDTO updateUser(UserDTO userDto) {
        User newUser = UserDTO.mapUserDtoToUser(userDto);
        userRepository.saveAndFlush(newUser);
        return userDto;

    }

    /**
     * fetch User with retrieved List<Link> with mail
     *
     * @param userId is a user email
     */
    public User getUserWithLinks(String userId) {
        User user = userRepository.fetchUserWithLinksAndComments(userId)
                .orElseThrow(() -> UserDetailsServiceImpl.throwUserNameNotFoundException(userId));
        Set<Link> linksWithComments = new HashSet<>(this.fillLinkWithSuitableComments(user.getUserLinks()));
        user.setUserLinks(linksWithComments);
        return user;
    }

    /**
     * find user for activation
     */
    public Optional<User> findUserByMailAndActivationCode(String mail, String code) {
        LOGGER.info("FIND USER BY MAIL {} AND ACTIVATION_CODE {}", mail, code);
        return userRepository.findByEmailAndActivationCode(mail, code);
    }

    public void sendWelcomeEmail(UserDTO user) throws ServiceException {
        mailService.sendWelcomeEmail(user);
    }

    @Transactional(readOnly = false)
    public void saveNewActionCode(User user) throws ServiceException {
        user.setActivationCode(String.valueOf(UUID.randomUUID()));
        user.setActivationDeadLineDate(LocalDateTime.now());
        final User savedUser = save(user);
        if (!savedUser.getActivationCode().equals(user.getActivationCode())) {
            // TODO: 19.04.2022 überprüfen, ob hier die Exception geworfen wird
            GlobalControllerAdvisor.createServiceException("Could not save new activation code for user: " + user.getEmail());
        }
        mailService.sendRecoverEmail(UserDTO.mapUserToUserDto(user));
    }

    @Transactional(readOnly = false)
    public User save(User user) {
        userRepository.saveAndFlush(user);
        User savedUser = (User) userDetailsService.loadUserByUsername(user.getEmail());
        LOGGER.info("User has been saved {}", savedUser);
        return savedUser;
    }

    @Transactional(readOnly = false)
    public void saveUsers(User... users) {
        Arrays.asList(users).forEach(user -> {
            LOGGER.info("TRY TO SAVE USER {}", user.getEmail());
            User userSaved = userRepository.saveAndFlush(user);
            LOGGER.info("USER SAVED WITH ID {} AND USERNAME {} ", userSaved.getUserId(), userSaved.getEmail());
        });
    }

    /**
     * find somebody by username
     *
     * @param username from searched user
     */
    @Cacheable("userInfo")
    public Optional<User> findUserById(String username) {
        return Optional.of(getUser(username));
    }

    /**
     * fetch User with retrieved List<Comment> with mail
     *
     * @param userId is user email
     */
    public User getUserWithComments(String userId) {
        return userRepository.fetchUserWithComments(userId)
                .orElseThrow(() -> UserDetailsServiceImpl.throwUserNameNotFoundException(userId));
    }

    public boolean lockUser(String userName) throws ServiceException {
        User dbUser = (User) userDetailsService.loadUserByUsername(userName);
        dbUser.setEnabled(false);
        mailService.sendEmail(userName, "Account is locked", "account is locked", false);
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
     *
     * @param requestedUser is a account owner, no public visitor
     * @return either list of links oder empty list
     */
    public Set<Link> findUserClickedLinks(final String requestedUser) {
        Set<Link> links = Optional.ofNullable(userRepository.findClickedUserLinks(requestedUser))
                .orElseThrow(() -> UserDetailsServiceImpl.throwUserNameNotFoundException(requestedUser))
                .map(User::getUserClickedLinks)
                .orElse(Collections.emptySet());
        links = new HashSet<>(this.fillLinkWithSuitableComments(new HashSet<>(links)));
        return links;
    }

    @Transactional(readOnly = false)
    public boolean saveNewUserPicture(InputStream inputStream, User user) throws IOException {
        byte[] pictureBytes = FileNIO.readStreamToByte(inputStream);
        if (pictureBytes.length != 0) {
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

    public InputStream resizeUserPic(InputStream inputStream, String extension) throws IOException {
        final BufferedImage bufferedImage = ImageManager.simpleResizeImage(inputStream, TARGET_WIDTH);
        return new ByteArrayInputStream(FileNIO.readPictureToByteArray(bufferedImage, extension));
    }

    public static UserDTO buildAnonymousUser() {
        Supplier<UserDTO> userDTOSupplier = () -> UserDTO.builder()
                .firstName("Guest")
                .secondName("")
                .build();
        return userDTOSupplier.get();
    }
}
