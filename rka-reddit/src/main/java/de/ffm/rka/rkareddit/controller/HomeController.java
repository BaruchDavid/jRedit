package de.ffm.rka.rkareddit.controller;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.service.LinkService;

@Controller
@RequestMapping("/")
@SessionAttributes("user")
public class HomeController {

	
	private LinkService linkService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

	public HomeController(LinkService linkService) {
		this.linkService = linkService;

	}


	@GetMapping({"/",""})
	public String list(Model model) {	
		List<Link> links = linkService.findAllCommentsForEachLink();
		LOGGER.info("{} Links has been found", links.size());
		model.addAttribute("links",links);
		return "link/link_list";
		 
	}

}
