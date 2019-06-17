package de.ffm.rka.rkareddit.controller;


import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
	public String list(Model model, HttpSession session) {	
		model.addAttribute("user", session.getAttribute("loggedUser"));
		model.addAttribute("links",linkRepository.findAll());
		return "link/link_list";
		 
	}

}
