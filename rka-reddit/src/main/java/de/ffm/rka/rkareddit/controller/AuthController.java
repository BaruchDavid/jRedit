package de.ffm.rka.rkareddit.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.ffm.rka.rkareddit.domain.Role;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.repository.UserRepository;
import de.ffm.rka.rkareddit.service.UserService;

@Controller
public class AuthController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
	
	private UserService userService;

	public AuthController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping({"/login"})
	public String login(Model model) {		
		return "auth/login"; 
	}
	
	@GetMapping({"/profile"})
	public String showProfile(Model model) {		
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

