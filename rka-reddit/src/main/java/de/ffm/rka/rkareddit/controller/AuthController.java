package de.ffm.rka.rkareddit.controller;

import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.domain.validator.Validationgroups;
import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;
import de.ffm.rka.rkareddit.service.UserService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class AuthController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
	private static final String USER_DTO = "userDto";
	private static final String SUCCESS = "success";
	private static final String REGISTRATION = "/registration";
	private static final String REDIRECT_MESSAGE = "redirectMessage";
	private static final String BINDING_ERROR = "bindingError";
	private static final String VALIDATION_ERRORS = "validationErrors";
	private static final String ERROR_MESSAGE = "Update user validation Error: {} message: {}";
	private static final String REDIRECT_TO_PRIVATE_PROFILE = "redirect:/profile/private";
	private final UserService userService;
	private final UserDetailsServiceImpl userDetailsService;
	
	public AuthController(UserService userService, UserDetailsServiceImpl userDetailsService) {
		this.userService = userService;
		this.userDetailsService = userDetailsService;
	}

	/**
	 * method for login and logout
	 * during logout, request parameter contains 'logout' param
	 * after session-timeout, you will be redirected to login again
	 * @return view for login / logout
	 */
	@GetMapping({"/login"})
	public String login(HttpServletRequest request) {
		LOGGER.info("login view {}",request.getRequestURI());
		return "auth/login";
	}
		
	/**
	 * set user info, user links and their comments
	 * @throws UsernameNotFoundException on non exists user
	 */
	@GetMapping(value={"/profile/private", "/profile/public/{email:.+}"})
	public String profile(@AuthenticationPrincipal UserDetails userPrincipal,
								@PathVariable(required = false) String email, 
								Model model) throws UsernameNotFoundException {
		Optional<UserDetails> authenticatedUser = Optional.ofNullable(userPrincipal);
		email = authenticatedUser.isPresent() && email == null ? authenticatedUser.get().getUsername() : email;
		User pageContentUser = Optional.ofNullable(userService.getUserWithLinks(email))
												.orElseThrow(()-> new UsernameNotFoundException("user not found"));		
		authenticatedUser.ifPresent(user -> {
			UserDTO member = userDetailsService.mapUserToUserDto(user.getUsername());
			model.addAttribute(USER_DTO, member);
		});
		
		List<Link> userLinks = Optional.ofNullable(pageContentUser.getUserLinks()).orElse(new ArrayList<>());
		
		List<Comment> userComments = Optional.ofNullable(userService.getUserWithComments(pageContentUser.getEmail()))
											.map(User::getUserComments)
											.orElse(new ArrayList<>());
		if (model.containsAttribute(SUCCESS)) {
			model.addAttribute(SUCCESS, true);
			model.addAttribute(REDIRECT_MESSAGE, model.asMap().get(REDIRECT_MESSAGE));
		}		
		model.addAttribute("userContent", pageContentUser);
		model.addAttribute("posts", userLinks);
		model.addAttribute("comments", userComments);
		return "auth/profile"; 
	}
	
	/**
	 * @param model to save userDto object
	 * @return view for registration
	 */
	@GetMapping(value = {REGISTRATION, REGISTRATION+"/"})
	public String registration(Model model) {	
		model.addAttribute(USER_DTO, UserDTO.builder().build());
		return "auth/register"; 
	}
	
	/**
	 * register user into system
	 * @return user
	 * @throws ServiceException will be triggered on any errors
	 */
	@PostMapping(value = {REGISTRATION})
	public String userRegistration(@Validated(value = {Validationgroups.ValidationUserRegistration.class}) UserDTO userDto,
								BindingResult bindingResult, RedirectAttributes attributes, HttpServletResponse res, 
								HttpServletRequest req, Model model) throws ServiceException {
		LOGGER.info("TRY TO REGISTER {}", userDto);
		if(bindingResult.hasErrors()) {
			return manageValidationErrors(userDto, bindingResult, res, req, model);
		} else {
			userDto = userService.register(userDto);
			model.addAttribute(USER_DTO, userDto);
			attributes.addFlashAttribute(SUCCESS, true);
			LOGGER.info("REGISTER SUCCESSFULLY {}", userDto);
			return  "redirect:".concat(REGISTRATION);
		}
	}



	/**
	 * user changes own email address
	 * @return new userDto object and success
	 * @throws ServiceException will be triggered on any errors
	 */
	@PatchMapping(value = {"/profile/private/me/update/email/{email:.+}"})
	public String userChangeEmail(@Validated(value = {Validationgroups.ValidationUserChangeEmail.class}) UserDTO userDto,
								BindingResult bindingResult, RedirectAttributes attributes, HttpServletResponse res, 
								HttpServletRequest req, Model model) throws ServiceException {
		LOGGER.info("TRY TO CHANGE EMAIL OF USER {}",userDto);
		if(bindingResult.hasErrors()) {
			return manageValidationErrors(userDto, bindingResult, res, req, model);
		} else {
			final String newEmail = userDto.getNewEmail();
			userDto = userDetailsService.mapUserToUserDto(userDto.getEmail());
			userDto.setNewEmail(newEmail);
			userDto = userService.emailChange(userDto);
			attributes.addFlashAttribute(SUCCESS, true);
			attributes.addFlashAttribute(REDIRECT_MESSAGE, "you got email, check it out!");
			LOGGER.info("CHANGE EMAIL SUCCESSFULLY {}", userDto);
			return REDIRECT_TO_PRIVATE_PROFILE;
		}
	}
	
	@GetMapping(value={"/activation/{email}/{activationCode}", "/mailchange/{email}/{activationCode}"})
	public String accountActivation(@PathVariable String email, @PathVariable String activationCode, 
									HttpServletRequest req, Model model, RedirectAttributes attributes) throws ServiceException {	
		LOGGER.info("TRY TO ACTIVATE ACCOUNT {}", email);
		boolean isNewEmail = false;
		String returnLink = "auth/activated";
		if(req.getRequestURI().contains("mailchange")) {
			isNewEmail = true;
			returnLink = "redirect:/profile/private";
			attributes.addFlashAttribute(REDIRECT_MESSAGE, "your new email has been activated");
			attributes.addFlashAttribute(SUCCESS,true);
		} 
		Optional<UserDTO> userDTO = userService.emailActivation(email, activationCode, isNewEmail);
		
		if(userDTO.isPresent()) {
			model.addAttribute(USER_DTO, userDTO.get());
			LOGGER.info("USER {} HAS BEEN ACTIVATED SUCCESSFULLY", email);
			return returnLink; 
		}else {
			LOGGER.error("USER {} WITH ACTIVATION-CODE {} HAS BEEN NOT ACTIVATED SUCCESSFULLY", email, activationCode);
			return "redirect:/error/registrationError"; 
		}
	}
	
	@PutMapping("/profile/private/me/update")
    public String user(@Validated(Validationgroups.ValidationChangeUserProperties.class) UserDTO userDto,
    								 BindingResult bindingResult, HttpServletResponse res, RedirectAttributes attributes,
    								 @AuthenticationPrincipal UserDetails userDetails, Model model)    {
		if(bindingResult.hasErrors()) {
			manageValidationErrors(userDto, bindingResult, res, attributes, model);
			return "redirect:/profile/private/me/".concat(userDto.getEmail());
		} else {
			userDto.setEmail(userDetails.getUsername());
			userService.changeUserDetails(userDto);
			attributes.addFlashAttribute(SUCCESS,true);
			attributes.addFlashAttribute(REDIRECT_MESSAGE,"your profile has been updated");
			res.setStatus(HttpStatus.PERMANENT_REDIRECT.value());
			LOGGER.info("USER CHANGED SUCCESSFULLY {}",userDto);
			return REDIRECT_TO_PRIVATE_PROFILE;
		}
    }


	/**
	 * validates input for user or password changes
	 * @param userDto saves failed user object
	 * @param bindingResult contains errors
	 * @param res set status
	 * @param attributes set attributes for redirect
	 * @param model saves userDto
	 */
	private void manageValidationErrors(@Validated(Validationgroups.ValidationChangeUserProperties.class) UserDTO userDto,
										BindingResult bindingResult, HttpServletResponse res,
										RedirectAttributes attributes, Model model) {
		bindingResult.getAllErrors().forEach(error -> LOGGER.warn( ERROR_MESSAGE,
											error.getCodes(), error.getDefaultMessage()));
		model.addAttribute(VALIDATION_ERRORS, bindingResult.getAllErrors());
		model.addAttribute(USER_DTO, userDto);
		attributes.addFlashAttribute(BINDING_ERROR,true);
		res.setStatus(HttpStatus.BAD_REQUEST.value());
	}

	/**
	 * validates input for registration or email change
	 * @param userDto contains failed object
	 * @param bindingResult contains all errors
	 * @param res set status
	 * @param req current request
	 * @param model saves userDto model and errors
	 * @return either registration view or email change view
	 */
	private String manageValidationErrors(@Validated({Validationgroups.ValidationUserRegistration.class,
			Validationgroups.ValidationUserChangeEmail.class}) UserDTO userDto, BindingResult bindingResult, HttpServletResponse res, HttpServletRequest req, Model model) {
		bindingResult.getAllErrors().forEach(error -> LOGGER.warn( "Register validation Error: {} during registration: {}",
				error.getCodes(), error.getDefaultMessage()));
		model.addAttribute(VALIDATION_ERRORS, bindingResult.getAllErrors());
		model.addAttribute(USER_DTO, userDto);
		res.setStatus(HttpStatus.BAD_REQUEST.value());
		return req.getRequestURI().contains("registration")? "auth/register": "auth/emailChange";
	}


	@PutMapping("/profile/private/me/{email:.+}/password")
    public String userPasswordChange(@Validated(Validationgroups.ValidationUserChangePassword.class) UserDTO userDto, 
    								 BindingResult bindingResult, HttpServletResponse res, RedirectAttributes attributes,
    								 @AuthenticationPrincipal UserDetails userDetails, Model model)    {
		
		if(bindingResult.hasErrors() || userDto.getPassword().equals(userDto.getNewPassword())) {
			manageValidationErrors(userDto, bindingResult, res, attributes, model);
			return "auth/passwordChange";
		} else {
			userDto.setEmail(userDetails.getUsername());
			userService.changeUserPassword(userDto);
			attributes.addFlashAttribute(SUCCESS,true);
			attributes.addFlashAttribute(REDIRECT_MESSAGE,"your password has been changed!");
			res.setStatus(HttpStatus.PERMANENT_REDIRECT.value());
			LOGGER.info("USER PASSWORD CHANGED SUCCESSFULLY {}",userDto);
			return REDIRECT_TO_PRIVATE_PROFILE;
		}
    }
	
	@GetMapping("/profile/private/me/{email:.+}")
	public String userInfo(@PathVariable String email, Model model) {
		model.addAttribute(USER_DTO, userDetailsService.mapUserToUserDto(email));
		return "auth/profileEdit";
	}
	
	@GetMapping("/profile/private/me/{email:.+}/password")
	public String changePassword(@PathVariable String email, Model model) {
		model.addAttribute(USER_DTO, userDetailsService.mapUserToUserDto(email));
		return "auth/passwordChange";
	}
	
	@GetMapping("/profile/private/me/update/email/{email:.+}")
	public String userEmailUpdateView(@PathVariable String email, Model model) {
		UserDTO user = userDetailsService.mapUserToUserDto(email);
		user.setNewEmail(StringUtils.EMPTY);
		model.addAttribute(USER_DTO, user);
		return "auth/emailChange";
	}
}

