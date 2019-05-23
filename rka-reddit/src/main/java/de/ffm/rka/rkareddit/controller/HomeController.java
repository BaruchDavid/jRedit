package de.ffm.rka.rkareddit.controller;


import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.repository.LinkRepository;

@Controller
@RequestMapping("/")
public class HomeController {

	
	private LinkRepository linkRepository;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

	public HomeController(LinkRepository linkRepository) {
		this.linkRepository = linkRepository;

	}


	@GetMapping({"/",""})
	public String list(Model model) {	
		
		model.addAttribute("links",linkRepository.findAll());
		return "link/link_list";
		 
	}

}
