package de.ffm.rka.rkareddit.controller;


import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.repository.CommentRepository;
import de.ffm.rka.rkareddit.repository.LinkRepository;
import de.ffm.rka.rkareddit.security.Role;

@Controller
@RequestMapping("/links")
public class LinkController {

	
	private LinkRepository linkRepository;
	private CommentRepository commentRepository;
	
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LinkController.class);

	public LinkController(LinkRepository linkRepository, CommentRepository commentRepository) {
		this.linkRepository = linkRepository;
		this.commentRepository = commentRepository;

	}


	@GetMapping({"/",""})
	public String list(Model model) {	
		
		model.addAttribute("links",linkRepository.findAll());
		return "link/link_list";
		 
	}
	
	
	@GetMapping("link/{linkId}")
	public String read(Model model, @PathVariable Long linkId) {		
		Optional<Link> link = linkRepository.findById(linkId);
		if(link.isPresent()) {
			Link currentLink = link.get();
			Comment comment = new Comment();
			comment.setLink(currentLink);
			model.addAttribute("link",currentLink);
			model.addAttribute("comment",comment);
			model.addAttribute("success",model.containsAttribute("success"));
			return "link/link_view";
		}else {
			return "redirect:/links";
		}
	}
	
	@Secured({"ROLE_ADMIN"})
	@GetMapping("/link/create")
	public String createNewLink(Model model) {
		model.addAttribute("newLink", new Link());
		return "link/submit";
	}
	
	@Secured({"ROLE_ADMIN"})
	@PostMapping("/link/create")
	public String saveNewLink(@Valid Link link, Model model, BindingResult bindingResult, RedirectAttributes redirectAttributes) {		
		
		if(bindingResult.hasErrors()) {
			LOGGER.info("Validation failed of link: {}", link.toString());
			model.addAttribute("newLink", link);
			return "link/submit";
		} else {
			linkRepository.saveAndFlush(link);
			redirectAttributes.addAttribute("linkId", link.getLinkId())
								.addFlashAttribute("success", true);
			return "redirect:/links/link/{linkId}";
		}
	}	
	
	@Secured({"ROLE_ADMIN"})
	@PostMapping("/link/comments")
	public String saveNewComment(@Valid Comment comment, Model model, BindingResult bindingResult, RedirectAttributes attributes) {		
		
		if(bindingResult.hasErrors()) {
			LOGGER.info("Validation failed of link: {}", comment.toString());
			model.addAttribute("newLink", comment);
			return "link/submit";
		} else {
			LOGGER.info("Saved comment {} for  link: {}", comment.toString());
			attributes.addFlashAttribute("success", true);
			commentRepository.saveAndFlush(comment);
			
			return "redirect:/links/link/".concat(comment.getLink().getLinkId().toString());
		}
	}	
}
