package de.ffm.rka.rkareddit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import de.ffm.rka.rkareddit.service.UserService;

@Controller
public class AuthController {
	
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
	
	@GetMapping({"/register"})
	public String register(Model model) {		
		return "auth/register"; 
	}
}

