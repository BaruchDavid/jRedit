package de.ffm.rka.rkareddit.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.service.UserService;



@Controller
public class AuthController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
	
	private UserService userService;
	private ModelMapper modelMapper;
	public AuthController(UserService userService, ModelMapper modelMapper) {
		this.userService = userService;
		this.modelMapper = modelMapper;
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
	public String showProfile(@AuthenticationPrincipal UserDetails userPrincipal,
								@PathVariable(required = false) String email, 
								Model model) {
		Optional<UserDetails> authenticatedUser = Optional.ofNullable(userPrincipal);	
		email = authenticatedUser.isPresent() && email == null ? authenticatedUser.get().getUsername() : email;
		UserDTO member = new UserDTO();
		User pageContentUser = Optional.ofNullable(userService.getUserWithLinks(email))
												.orElseThrow(()-> new UsernameNotFoundException("user not found"));
		if(authenticatedUser.isPresent()) {
			
			User usr = userService.getUserWithLinks(authenticatedUser.get().getUsername());
			member = UserDTO.builder()
							  .firstName(usr.getFirstName())
							  .secondName(usr.getSecondName())
							  .build();
		}
		List<Link> userLinks = Optional.ofNullable(pageContentUser.getUserLinks()).orElse(new ArrayList<Link>());	
		List<Comment> userComments = Optional.ofNullable(userService.getUserWithComments(pageContentUser.getEmail())
																	.getUserComments())
											.orElse(new ArrayList<Comment>());
		model.addAttribute("userDto", member);
		model.addAttribute("userContent", pageContentUser);
		model.addAttribute("posts", userLinks);
		model.addAttribute("comments", userComments);
		return "auth/profile"; 
	}
	
	/**
	 * @param model
	 * @return
	 */
	@GetMapping({"/register"})
	public String register(Model model) {	
		model.addAttribute("user", new User());
		return "auth/register"; 
	}
	
	/**
	 * register user into system
	 * @return user
	 */
	@PostMapping("/register")
	public String registerNewUser(@Valid UserDTO userDto, BindingResult bindingResult, RedirectAttributes attributes, HttpServletResponse res, Model model) {
		LOGGER.info("TRY TO REGISTER {}",userDto);
		if(bindingResult.hasErrors()) {
			final int badRequest = 400;
			LOGGER.warn("Validation Error during registration {}", bindingResult.getAllErrors());
			model.addAttribute("validationErrors", bindingResult.getAllErrors());
			model.addAttribute("user", userDto);
			res.setStatus(badRequest);
			return "auth/register";
		} else {
			userService.register(userDto);
			attributes.addFlashAttribute("success",true);	
			return "redirect:/register";
		}
	}
	
	@GetMapping({"/activate/{email}/{activationCode}"})
	public String activateAccount(@PathVariable String email, @PathVariable String activationCode, Model model) {	
		LOGGER.info("TRY TO ACTIVATE ACCOUNT {}", email);
		Optional<User> user = userService.findUserByMailAndActivationCode(email, activationCode);
		if(user.isPresent()) {			
			User newUser = user.get();
			newUser.setEnabled(true);
			newUser.setConfirmPassword(newUser.getPassword());
			userService.save(newUser);
			UserDTO userDTO = modelMapper.map(user.get(), UserDTO.class);
			userService.sendWelcomeEmail(userDTO);
			LOGGER.info("USER {} HAS BEEN ACTIVATED SUCCESSFULLY", email);
			model.addAttribute("user", userDTO);
			return "auth/activated"; 
		}else {
			LOGGER.info("USER {} HAS BEEN NOT ACTIVATED SUCCESSFULLY", email);
			return "redirect:/"; 
		}
	}
}

