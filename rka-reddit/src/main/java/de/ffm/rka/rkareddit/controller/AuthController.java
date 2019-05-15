package de.ffm.rka.rkareddit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

	@GetMapping({"/login"})
	public String list(Model model) {		
		return "auth/login"; 
	}
	
}
