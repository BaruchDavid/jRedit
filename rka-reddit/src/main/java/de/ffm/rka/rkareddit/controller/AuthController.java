package de.ffm.rka.rkareddit.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.domain.validator.Validationgroups;
import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;
import de.ffm.rka.rkareddit.service.UserService;

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
	private static final String REDIRECT_TO_PRIVATE_PROFIL = "redirect:/profile/private";
	private static final String NO_USER_FOR_PROFILE_VIEW = "User not found for profile view";
	private UserService userService;
	private UserDetailsServiceImpl userDetailsService;
	
	public AuthController(UserService userService, UserDetailsServiceImpl userDetailsService) {
		this.userService = userService;
		this.userDetailsService = userDetailsService;
	}

	/**
	 * method for login and logout
	 * during logout, request parameter contains 'logout' param
	 * after session-timeout, you will be redirected to login again
	 * @param request
	 * @return view for login / logout
	 */
	@GetMapping({"/login"})
	public String login(HttpServletRequest request) {
		return "auth/login"; 
	}
		
	/**
	 * set user info, user links and their comments
	 * @throws Exception 
	 */
	@GetMapping(value={"/profile/private", "/profile/public/{email:.+}"})
	public String profile(@AuthenticationPrincipal UserDetails userPrincipal,
								@PathVariable(required = false) String email, 
								Model model) {
		Optional<UserDetails> authenticatedUser = Optional.ofNullable(userPrincipal);
		email = authenticatedUser.isPresent() && email == null ? authenticatedUser.get().getUsername() : email;
		User pageContentUser = Optional.ofNullable(userService.getUserWithLinks(email))
												.orElseThrow(()-> new UsernameNotFoundException("user not found"));		
		authenticatedUser.ifPresent(user -> {
			UserDTO member = userDetailsService.mapUserToUserDto(user.getUsername());
			model.addAttribute(USER_DTO, member);
		});
		
		List<Link> userLinks = Optional.ofNullable(pageContentUser.getUserLinks()).orElse(new ArrayList<Link>());	
		List<Comment> userComments = Optional.ofNullable(userService.getUserWithComments(pageContentUser.getEmail())
																	.getUserComments())
											.orElse(new ArrayList<Comment>());
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
	 * @param model
	 * @return
	 */
	@GetMapping(value = {REGISTRATION, REGISTRATION+"/"})
	public String registration(Model model) {	
		model.addAttribute(USER_DTO, UserDTO.builder().build());
		return "auth/register"; 
	}
	
	/**
	 * register user into system
	 * @return user
	 * @throws ServiceException 
	 */
	@PostMapping(value = {REGISTRATION})
	public String userRegistration(@Validated(value = {Validationgroups.ValidationUserRegistration.class,
														Validationgroups.ValidationUserChangeEmail.class}) UserDTO userDto, 
								BindingResult bindingResult, RedirectAttributes attributes, HttpServletResponse res, 
								HttpServletRequest req, Model model) throws ServiceException {
		LOGGER.info("TRY TO REGISTER {}",userDto);
		if(bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach(error -> LOGGER.warn( "Register validation Error: {} during registration: {}", 
												error.getCodes(), error.getDefaultMessage()));
			model.addAttribute(VALIDATION_ERRORS, bindingResult.getAllErrors());
			model.addAttribute(USER_DTO, userDto);
			res.setStatus(HttpStatus.BAD_REQUEST.value());
			return req.getRequestURI().contains("registration")? "auth/register": "auth/emailChange";
		} else {
			userDto = userService.register(userDto);
			model.addAttribute(USER_DTO, userDto);
			attributes.addFlashAttribute(SUCCESS, true);
			LOGGER.info("REGISTER SUCCESSFULY {}", userDto);
			return  "redirect:/registration";
		}
	}
	
	/**
	 * user changes own email address
	 * @return new userDto object and success
	 * @throws ServiceException
	 */
	@PatchMapping(value = {"/profile/private/me/update/email/{email:.+}"})
	public String userChangeEmail(@Validated(value = {Validationgroups.ValidationUserRegistration.class,
														Validationgroups.ValidationUserChangeEmail.class}) UserDTO userDto, 
								BindingResult bindingResult, RedirectAttributes attributes, HttpServletResponse res, 
								HttpServletRequest req, Model model) throws ServiceException {
		LOGGER.info("TRY TO REGISTER {}",userDto);
		if(bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach(error -> LOGGER.warn( "Register validation Error: {} during registration: {}", 
												error.getCodes(), error.getDefaultMessage()));
			model.addAttribute(VALIDATION_ERRORS, bindingResult.getAllErrors());
			model.addAttribute(USER_DTO, userDto);
			res.setStatus(HttpStatus.BAD_REQUEST.value());
			return req.getRequestURI().contains("registration")? "auth/register": "auth/emailChange";
		} else {
			final String newEmail = userDto.getNewEmail();
			userDto = userDetailsService.mapUserToUserDto(userDto.getEmail());
			userDto.setNewEmail(newEmail);
			userDto = userService.emailChange(userDto);
			attributes.addFlashAttribute(SUCCESS, true);
			attributes.addFlashAttribute(REDIRECT_MESSAGE, "you got email, check it out!");
			LOGGER.info("CHANGE EMAIL SUCCESSFULY {}", userDto);
			return REDIRECT_TO_PRIVATE_PROFIL;
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
    public String user(@Validated(Validationgroups.ValidationChangeUserGroup.class) UserDTO userDto, 
    								 BindingResult bindingResult, HttpServletResponse res, RedirectAttributes attributes,
    								 @AuthenticationPrincipal UserDetails userDetails, Model model)    {
		if(bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach(error -> LOGGER.warn( ERROR_MESSAGE, 
												error.getCodes(), error.getDefaultMessage()));
			model.addAttribute(VALIDATION_ERRORS, bindingResult.getAllErrors());
			model.addAttribute(USER_DTO, userDto);
			attributes.addFlashAttribute(BINDING_ERROR,true);
			res.setStatus(HttpStatus.BAD_REQUEST.value());
			return "redirect:/profile/private/me/".concat(userDto.getEmail());
		} else {
			userDto.setEmail(userDetails.getUsername());
			userService.changeUserDetails(userDto);
			attributes.addFlashAttribute(SUCCESS,true);
			attributes.addFlashAttribute(REDIRECT_MESSAGE,"your profile has been updated");
			res.setStatus(HttpStatus.PERMANENT_REDIRECT.value());
			LOGGER.info("USER CHAGEND SUCCESSFULY {}",userDto);
			return REDIRECT_TO_PRIVATE_PROFIL;
		}
    }
	
		
	@PutMapping("/profile/private/me/{email:.+}/password")
    public String userPasswordChange(@Validated(Validationgroups.ValidationUserChangePassword.class) UserDTO userDto, 
    								 BindingResult bindingResult, HttpServletResponse res, RedirectAttributes attributes,
    								 @AuthenticationPrincipal UserDetails userDetails, Model model)    {
		
		if(bindingResult.hasErrors() || userDto.getPassword().equals(userDto.getNewPassword())) {		
			bindingResult.getAllErrors().forEach(error -> LOGGER.warn( ERROR_MESSAGE, 
												error.getCodes(), error.getDefaultMessage()));
			model.addAttribute(VALIDATION_ERRORS, bindingResult.getAllErrors());
			model.addAttribute(USER_DTO, userDto);
			attributes.addFlashAttribute(BINDING_ERROR,true);
			res.setStatus(HttpStatus.BAD_REQUEST.value());
			return "auth/passwordChange";
		} else {
			userDto.setEmail(userDetails.getUsername());
			userService.changeUserPassword(userDto);
			attributes.addFlashAttribute(SUCCESS,true);
			attributes.addFlashAttribute(REDIRECT_MESSAGE,"your password has been changed!");
			res.setStatus(HttpStatus.PERMANENT_REDIRECT.value());
			LOGGER.info("USER PASSWORD CHAGEND SUCCESSFULY {}",userDto);
			return REDIRECT_TO_PRIVATE_PROFIL;
		}
    }
	
	@GetMapping("/profile/private/me/{email:.+}")
	public String userInfo(@PathVariable String email, HttpServletResponse response, Model model) {
		model.addAttribute(USER_DTO, userDetailsService.mapUserToUserDto(email));
		return "auth/profileEdit";
	}
	
	@GetMapping("/profile/private/me/{email:.+}/password")
	public String changePassword(@PathVariable String email, HttpServletResponse response, Model model) {
		model.addAttribute(USER_DTO, userDetailsService.mapUserToUserDto(email));
		return "auth/passwordChange";
	}
	
	@GetMapping("/profile/private/me/update/email/{email:.+}")
	public String userEmailUpdateView(@PathVariable String email, HttpServletResponse response, Model model) {
		UserDTO user = userDetailsService.mapUserToUserDto(email);
		user.setNewEmail(StringUtils.EMPTY);
		model.addAttribute(USER_DTO, user);
		return "auth/emailChange";
	}
}

