package de.ffm.rka.rkareddit.controller;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.CommentDTO;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.domain.validator.user.UserValidationGroup;
import de.ffm.rka.rkareddit.exception.GlobalControllerAdvisor;
import de.ffm.rka.rkareddit.exception.RegisterException;
import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.service.PostService;
import de.ffm.rka.rkareddit.service.UserDetailsServiceImpl;
import de.ffm.rka.rkareddit.service.UserService;
import de.ffm.rka.rkareddit.util.CacheController;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class AuthController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    private static final String LOGGED_IN_USER = "userDto";
    private static final String CONTENT_USER = "userContent";
    private static final String SUCCESS = "success";
    private static final String REDIRECT_MESSAGE = "redirectMessage";
    private static final String BINDING_ERROR = "bindingError";
    private static final String VALIDATION_ERRORS = "validationErrors";
    private static final String ERROR_MESSAGE = "Update user validation Error: {} message: {}";
    private static final String REDIRECT_TO_PRIVATE_PROFILE = "redirect:/profile/private";
    private static final String NOT_LOGGED_IN = "";
    private static final String USER_VISIT_NO_CACHE_CONTROL = "cacheControl";
    private static final String REACTIVATION_FAILED = "USER %s WITH REACTIVATION-CODE %s HAS BEEN FAILED";


    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;
    private final PostService postService;
    private final CacheController cacheController;

    public AuthController(UserService userService, UserDetailsServiceImpl userDetailsService,
                          PostService postService, CacheController cacheController) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.postService = postService;
        this.cacheController = cacheController;
    }

    /**
     * method for login and logout
     * during logout, request parameter contains 'logout' param
     * after session-timeout, you will be redirected to login again
     *
     * @return view for login / logout
     */
    @GetMapping({"/login"})
    public String login(HttpServletRequest request) {
        LOGGER.info("login view {}", request.getRequestURI());
        return "auth/login";
    }

    /**
     * set user info, user links and their comments
     *
     * @throws UsernameNotFoundException on non exists user
     */
    @GetMapping(value = {"/profile/private",
            "/profile/{email}/links",
            "/profile/public/{email:.+}"})
    public String profile(@AuthenticationPrincipal UserDetails userPrincipal,
                          @PathVariable(required = false) String email,
                          Model model) {
        final User pageContentUser = createContentUser(model, email, userPrincipal);
        Set<LinkDTO> userLinks = Optional.ofNullable(pageContentUser.getUserLinks())
                .orElse(Collections.emptySet())
                .stream()
                .map(LinkDTO::mapFullyLinkToDto)
                .collect(Collectors.toSet());
        final int commentSize = Optional.ofNullable(pageContentUser.getUserComment())
                .orElse(Collections.emptySet()).size();
        if (model.containsAttribute(SUCCESS)) {
            model.addAttribute(SUCCESS, true);
            model.addAttribute(REDIRECT_MESSAGE, model.asMap().get(REDIRECT_MESSAGE));
        }
        UserDTO contentUser = UserDTO.mapUserToUserDto(pageContentUser);
        model.addAttribute(CONTENT_USER, contentUser);
        model.addAttribute("posts", userLinks);
        model.addAttribute("commentCount", commentSize);
        model.addAttribute("userSince", DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                .format(contentUser.getCreationDate()));
        return "auth/profileLinks";
    }

    private User createContentUser(Model model, String emailForContent, UserDetails userPrincipal) {

        Optional<UserDetails> authenticatedUser = Optional.ofNullable(userPrincipal);
        emailForContent = authenticatedUser.isPresent() && emailForContent == null ?
                authenticatedUser.get().getUsername() : emailForContent;
        User pageContentUser = Optional.ofNullable(userService.getUserWithLinks(emailForContent))
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        String authenticatedUserName = authenticatedUser.map(UserDetails::getUsername)
                .orElse(NOT_LOGGED_IN);

        if (pageContentUser.getUsername().equals(authenticatedUserName)) {
            model.addAttribute(USER_VISIT_NO_CACHE_CONTROL, cacheController.setCacheHeader(authenticatedUserName));
        } else {
            model.addAttribute(USER_VISIT_NO_CACHE_CONTROL, cacheController.setCacheHeader(StringUtils.EMPTY));
        }

        if (!authenticatedUserName.isEmpty()) {
            model.addAttribute(LOGGED_IN_USER, UserDTO.mapUserToUserDto((User)
                    userDetailsService.loadUserByUsername(authenticatedUserName)));
        } else {
            model.addAttribute(LOGGED_IN_USER, new UserDTO());
        }
        return pageContentUser;
    }

    @GetMapping(value = {"/profile/{email}/comments"})
    public String profileWithComponents(@AuthenticationPrincipal UserDetails userPrincipal,
                                        @PathVariable(required = false) String email,
                                        Model model) {
        final User pageContentUser = createContentUser(model, email, userPrincipal);
        UserDTO contentUser = UserDTO.mapUserToUserDto(pageContentUser);
        final Set<CommentDTO> comments = postService.findUserComments(contentUser.getEmail());
        final int linkSize = Optional.ofNullable(pageContentUser.getUserLinks())
                .orElse(Collections.emptySet()).size();
        model.addAttribute(CONTENT_USER, contentUser);
        model.addAttribute("postsCount", linkSize);
        model.addAttribute("comments", comments);
        model.addAttribute("userSince", DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                .format(contentUser.getCreationDate()));
        return "auth/profileComments";
    }

    /**
     * user changes own email address
     *
     * @return new userDto object and success
     */
    @PatchMapping(value = {"/profile/private/me/update/email"})
    public String userChangeEmail(@Validated(value = {UserValidationGroup.ValidationUserChangeEmail.class}) UserDTO userDto,
                                  BindingResult bindingResult, RedirectAttributes attributes,
                                  @AuthenticationPrincipal UserDetails userDetails, HttpServletResponse res,
                                  HttpServletRequest req, Model model) throws ServiceException {
        LOGGER.info("TRY TO CHANGE EMAIL OF USER {}", userDto);
        if (bindingResult.hasErrors()) {
            setSameUserForLoginAndContent((User) userDetails, model);
            return manageValidationErrors(userDto, bindingResult, res, req, model);
        } else {
            userDto = userService.userEmailChange(userDto);
            attributes.addFlashAttribute(SUCCESS, true);
            attributes.addFlashAttribute(REDIRECT_MESSAGE, "you got email, check it out!");
            LOGGER.info("CHANGE EMAIL SUCCESSFULLY {}", userDto);
            return REDIRECT_TO_PRIVATE_PROFILE;
        }
    }

    @GetMapping(value = {"/mailchange/{email}/{activationCode}"})
    public String emailActivation(@PathVariable String email, @PathVariable String activationCode,
                                  Model model, RedirectAttributes attributes) throws ServiceException, RegisterException {
        LOGGER.info("TRY TO ACTIVATE ACCOUNT WITH NEW EMAIL {}", email);
        Optional.ofNullable(userService.userEmailActivation(email, activationCode, true))
                .orElseThrow(() -> GlobalControllerAdvisor.createRegisterException(
                        String.format(REACTIVATION_FAILED, email, activationCode)));
        model.addAttribute(LOGGED_IN_USER, UserDTO.builder().build());
        LOGGER.info("USER {} HAS BEEN ACTIVATED SUCCESSFULLY", email);
        attributes.addFlashAttribute(SUCCESS, true);
        attributes.addFlashAttribute(REDIRECT_MESSAGE, "Your new email is active! Please login");
        userDetailsService.clearSecurityContext();
        return "redirect:".concat("/links");
    }


    @GetMapping("/recover/{email}/{activationCode}")
    public String getPasswordRecoveryView(@PathVariable String email, @PathVariable String activationCode, Model model)
            throws ServiceException {
        LOGGER.info("TRY TO ACTIVATE ACCOUNT {}", email);
        String returnLink = "recover/passwordRecovery";
        Optional<UserDTO> userDTO = userService.getUserForPasswordReset(email, activationCode);
        if (userDTO.isPresent()) {
            model.addAttribute(LOGGED_IN_USER, UserDTO.builder().email("notLoggedIn").build());
            model.addAttribute(CONTENT_USER, UserDTO.builder().firstName(userDTO.get().getFullName()).build());
            return returnLink;
        } else {
            LOGGER.error("USER {} WITH ACTIVATION-CODE {} HAS BEEN NOT ACTIVATED SUCCESSFULLY", email, activationCode);
            return "redirect:/error/registrationError";
        }
    }


    @PutMapping("/profile/private/me/update")
    public String user(@Validated(UserValidationGroup.ValidationChangeUserProperties.class) UserDTO userDto,
                       BindingResult bindingResult, HttpServletResponse res, RedirectAttributes attributes,
                       @AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (bindingResult.hasErrors()) {
            manageValidationErrors(userDto, bindingResult, res, attributes, model);
            setSameUserForLoginAndContent((User) userDetails, model);
            return "auth/profileEdit";
        } else {
            userDto.setEmail(userDetails.getUsername());
            userService.changeUserDetails(userDto);
            attributes.addFlashAttribute(SUCCESS, true);
            attributes.addFlashAttribute(REDIRECT_MESSAGE, "your profile has been updated");
            res.setStatus(HttpStatus.PERMANENT_REDIRECT.value());
            LOGGER.info("USER CHANGED SUCCESSFULLY {}", userDto);
            return REDIRECT_TO_PRIVATE_PROFILE;
        }
    }

    @PutMapping("/profile/private/me/password")
    public String userPasswordChange(@Validated(UserValidationGroup.ValidationUserChangePassword.class) UserDTO userDto,
                                     BindingResult bindingResult, HttpServletResponse res, RedirectAttributes attributes,
                                     @AuthenticationPrincipal UserDetails userDetails, Model model) throws ServiceException {
        if (bindingResult.hasErrors()) {
            manageValidationErrors(userDto, bindingResult, res, attributes, model);
            setSameUserForLoginAndContent((User) userDetails, model);
            return "auth/passwordChange";
        } else {
            userDto.setEmail(userDetails.getUsername());
            userService.changeUserPassword(userDto);
            attributes.addFlashAttribute(SUCCESS, true);
            attributes.addFlashAttribute(REDIRECT_MESSAGE, "your password has been changed!");
            res.setStatus(HttpStatus.PERMANENT_REDIRECT.value());
            LOGGER.info("USER PASSWORD CHANGED SUCCESSFULLY {}", userDto);
            return REDIRECT_TO_PRIVATE_PROFILE;
        }
    }

    @GetMapping("/profile/private/me")
    public String showViewForEmailChange(@AuthenticationPrincipal UserDetails user, Model model) {
        setSameUserForLoginAndContent((User) user, model);
        return "auth/profileEdit";
    }


    @GetMapping("/profile/private/me/password")
    public String changePassword(@AuthenticationPrincipal UserDetails user, Model model) {
        setSameUserForLoginAndContent((User) user, model);
        return "auth/passwordChange";
    }

    @GetMapping("/profile/private/me/update/email")
    public String userEmailUpdateView(@AuthenticationPrincipal UserDetails user, Model model) {
        UserDTO userDto = setSameUserForLoginAndContent((User) user, model);
        userDto.setNewEmail(StringUtils.EMPTY);
        return "auth/emailChange";
    }

    @GetMapping("/profile/user/recovering")
    public String showRecoverUserPwRequest(Model model) {
        model.addAttribute(LOGGED_IN_USER, UserDTO.builder().email("notLoggedIn").build());
        model.addAttribute(CONTENT_USER, UserDTO.builder().firstName("Guest").build());
        return "recover/recoverUserPwRequest";
    }

    /**
     * @param user  same user, who is logged in and creates content
     * @param model to save user for view
     * @return logged in user
     */
    private UserDTO setSameUserForLoginAndContent(User user, Model model) {
        final UserDTO userDTO = UserDTO.mapUserToUserDto(user);
        model.addAttribute(LOGGED_IN_USER, userDTO);
        model.addAttribute(CONTENT_USER, userDTO);
        return userDTO;
    }

    /**
     * validates input for registration or email change
     *
     * @param userDto       contains failed object
     * @param bindingResult contains all errors
     * @param res           set status
     * @param req           current request
     * @param model         saves userDto model and errors
     * @return either registration view or email change view
     */
    private String manageValidationErrors(@Validated({UserValidationGroup.ValidationUserRegistration.class,
            UserValidationGroup.ValidationUserChangeEmail.class}) UserDTO userDto, BindingResult bindingResult, HttpServletResponse res, HttpServletRequest req, Model model) {
        bindingResult.getAllErrors().forEach(error -> LOGGER.warn("Register validation Error: {} during registration: {}",
                error.getCodes(), error.getDefaultMessage()));
        model.addAttribute(VALIDATION_ERRORS, bindingResult.getAllErrors());
        model.addAttribute(LOGGED_IN_USER, userDto);
        res.setStatus(HttpStatus.BAD_REQUEST.value());
        return req.getRequestURI().contains("registration") ? "auth/register" : "auth/emailChange";
    }

    /**
     * validates input for user or password changes
     *
     * @param userDto       saves failed user object
     * @param bindingResult contains errors
     * @param res           set status
     * @param attributes    set attributes for redirect
     * @param model         saves userDto
     */
    private void manageValidationErrors(@Validated(UserValidationGroup.ValidationChangeUserProperties.class) UserDTO userDto,
                                        BindingResult bindingResult, HttpServletResponse res,
                                        RedirectAttributes attributes, Model model) {
        bindingResult.getAllErrors().forEach(error -> LOGGER.warn(ERROR_MESSAGE,
                error.getCodes(), error.getDefaultMessage()));
        model.addAttribute(VALIDATION_ERRORS, bindingResult.getAllErrors());
        model.addAttribute(LOGGED_IN_USER, userDto);
        attributes.addFlashAttribute(BINDING_ERROR, true);
        res.setStatus(HttpStatus.BAD_REQUEST.value());
    }
}