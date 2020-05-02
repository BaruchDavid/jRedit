package de.ffm.rka.rkareddit.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;
import de.ffm.rka.rkareddit.service.UserService;



@Controller
public class AuthController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
	private static final String USER_DTO = "userDto";
	private static final String SUCCESS = "success";
	private static final String BINDING_ERROR = "bindingError";
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
		
		model.addAttribute("userContent", pageContentUser);
		model.addAttribute("posts", userLinks);
		model.addAttribute("comments", userComments);
		return "auth/profile"; 
	}
	
	/**
	 * @param model
	 * @return
	 */
	@GetMapping({"/registration"})
	public String registration(Model model) {	
		model.addAttribute(USER_DTO, UserDTO.builder().build());
		return "auth/register"; 
	}
	
	/**
	 * register user into system
	 * @return user
	 */
	@PostMapping("/registration")
	public String user(@Valid UserDTO userDto, BindingResult bindingResult, RedirectAttributes attributes, HttpServletResponse res, Model model) {
		LOGGER.info("TRY TO REGISTER {}",userDto);
		if(bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach(error -> LOGGER.warn( "Register validation Error: {} during registration: {}", 
												error.getCodes(), error.getDefaultMessage()));
			model.addAttribute("validationErrors", bindingResult.getAllErrors());
			model.addAttribute("userDto", userDto);
			res.setStatus(HttpStatus.BAD_REQUEST.value());
			return "auth/register";
		} else {
			userService.register(userDto);
			attributes.addFlashAttribute(SUCCESS,true);
			LOGGER.info("REGISTER SUCCESSFULY{}",userDto);
			return "redirect:/registration";
		}
	}
	
	@GetMapping({"/activation/{email}/{activationCode}"})
	public String accountActivation(@PathVariable String email, @PathVariable String activationCode, Model model) {	
		LOGGER.info("TRY TO ACTIVATE ACCOUNT {}", email);
		Optional<User> user = userService.findUserByMailAndActivationCode(email, activationCode);
		if(user.isPresent()) {			
			User newUser = user.get();
			newUser.setEnabled(true);
			newUser.setConfirmPassword(newUser.getPassword());
			userService.save(newUser);
			UserDTO userDTO = userDetailsService.mapUserToUserDto(user.get().getUsername());
			userService.sendWelcomeEmail(userDTO);
			LOGGER.info("USER {} HAS BEEN ACTIVATED SUCCESSFULLY", email);
			model.addAttribute("user", userDTO);
			return "auth/activated"; 
		}else {
			LOGGER.info("USER {} HAS BEEN NOT ACTIVATED SUCCESSFULLY", email);
			return "redirect:/"; 
		}
	}
	
	@PutMapping("/profile/private/me/update")
    public String user(@Validated(UserDTO.ValidationChangeUserGroup.class) UserDTO userDto, 
    								 BindingResult bindingResult, HttpServletResponse res, RedirectAttributes attributes,
    								 Model model)    {
		if(bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach(error -> LOGGER.warn( "Update user validation Error: {} message: {}", 
												error.getCodes(), error.getDefaultMessage()));
			model.addAttribute("validationErrors", bindingResult.getAllErrors());
			model.addAttribute("user", userDto);
			attributes.addFlashAttribute(BINDING_ERROR,true);
			res.setStatus(HttpStatus.BAD_REQUEST.value());
			return "redirect:/profile/private/me/".concat(userDto.getEmail());
		} else {
			userService.changeUserDetails(userDto);
			attributes.addFlashAttribute(SUCCESS,true);
			res.setStatus(HttpStatus.OK.value());
			LOGGER.info("USER CHAGEND SUCCESSFULY{}",userDto);
			return "redirect:/profile/private/me/".concat(userDto.getEmail());
		}
    }
	
	@GetMapping("/profile/private/me/{email:.+}")
	public String userInfo(@PathVariable String email, HttpServletResponse response, Model model) {
		UserDTO user = Optional.ofNullable(userDetailsService.mapUserToUserDto(email))
								.orElseThrow(() -> new UsernameNotFoundException("User not found for profile view"));
		if (model.containsAttribute(SUCCESS)) {
			model.addAttribute(SUCCESS, true);
		} else if(model.containsAttribute(BINDING_ERROR)) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			model.addAttribute(BINDING_ERROR, true);
		}
		model.addAttribute(USER_DTO, user);
		return "auth/profileEdit";
	}
}

