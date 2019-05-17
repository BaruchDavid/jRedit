package de.ffm.rka.rkareddit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

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

