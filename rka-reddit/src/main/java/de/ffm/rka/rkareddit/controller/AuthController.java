package de.ffm.rka.rkareddit.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.service.UserService;



@Controller
public class AuthController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
	
	private UserService userService;

	public AuthController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * method for login and logout
	 * during logout, request parameter contains 'logout' param
	 * after session-timeout, you will be redirected to login again
	 * @param request
	 * @return view for login / logout
	 */
	@GetMapping({"/login"})
	public String login(HttpServletRequest request,  
						SessionStatus sessionStatus, Model model) {		
		String logout = request.getParameter("logout");
		if(logout!=null && logout.isEmpty()) {
			return "redirect:/links/cleanUp";
		}
		return "auth/login"; 
	}
		
	/**
	 * set user info, user links and their comments
	 * @throws Exception 
	 */
	@GetMapping(value={"/profile/private", "/profile/public/{email:.+}"})
	public String showProfile(@AuthenticationPrincipal UserDetails userPrincipal,
								@PathVariable(required = false) String email, 
								Model model) throws Exception {
		Optional<UserDetails> authenticatedUser = Optional.ofNullable(userPrincipal);	
		email = authenticatedUser.isPresent() && email == null ? authenticatedUser.get().getUsername() : email;
		User member = new User();
		User pageContentUser = Optional.ofNullable(userService.getUserWithLinks(email))
												.orElseThrow(()-> new UsernameNotFoundException("user not found"));
		if(authenticatedUser.isPresent()) {
			member = userService.getUserWithLinks(authenticatedUser.get().getUsername());
		}
		List<Link> userLinks = Optional.ofNullable(pageContentUser.getUserLinks()).orElse(new ArrayList<Link>());	
		List<Comment> userComments = Optional.ofNullable(userService.getUserWithComments(pageContentUser.getEmail())
																	.getUserComments())
											.orElse(new ArrayList<Comment>());
		model.addAttribute("user", member);
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
		//model.addAttribute("success", false);
		return "auth/register"; 
	}
	
	/**
	 * register user into system
	 * @return user
	 */
	@PostMapping("/register")
	public String registerNewUser(@Valid User user, BindingResult bindingResult, RedirectAttributes attributes, Model model) {
		LOGGER.info("TRY TO REGISTER {}",user.toString());
		if(bindingResult.hasErrors()) {
			LOGGER.warn("Validation Error during registration {}", bindingResult.getAllErrors());
			model.addAttribute("validationErrors", bindingResult.getAllErrors());
			model.addAttribute("user", user);
			return "auth/register";
		} else {
			User newUser = userService.register(user);
			attributes.addAttribute("id", newUser.getUserId())
						.addFlashAttribute("success",true);
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
			userService.sendWelcomeEmail(newUser);
			LOGGER.info("USER {} HAS BEEN ACTIVATED SUCCESSFULLY", email);
			return "auth/activated"; 
		}else {
			LOGGER.info("USER {} HAS BEEN NOT ACTIVATED SUCCESSFULLY", email);
			return "redirect:/"; 
		}
	}
}

