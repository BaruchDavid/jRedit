package de.ffm.rka.rkareddit.controller;


import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.repository.CommentRepository;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;
import de.ffm.rka.rkareddit.service.LinkService;
import de.ffm.rka.rkareddit.service.UserService;

@Controller
@RequestMapping("/links")
@SessionAttributes("user")
public class LinkController {

	private UserDetailsService userDetailService;
	private LinkService linkService;
	private CommentRepository commentRepository;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private UserService userService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LinkController.class);

	public LinkController(LinkService linkService, CommentRepository commentRepository,  UserDetailsService userDetailService) {
		this.linkService = linkService;
		this.commentRepository = commentRepository;
		this.userDetailService = userDetailService;

	}


	@GetMapping({"/",""})
	public String list(Model model, HttpSession session) {	
		model.addAttribute("links",linkService.findAll());
		return "link/link_list";
	}
	
	
	@GetMapping("link/{linkId}")
	public String read(Model model, @PathVariable Long linkId, HttpServletRequest request) {		
		Optional<Link> link = linkService.findLinkByLinkId(linkId);
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
	public String saveNewLink(@Valid Link link, Model model, HttpServletRequest request,
							BindingResult bindingResult, RedirectAttributes redirectAttributes) {		
		
		if(bindingResult.hasErrors()) {
			LOGGER.info("Validation failed of link: {}", link.toString());
			model.addAttribute("newLink", link);
			return "link/submit";
		} else {
			User user = (User) userDetailService.loadUserByUsername(request.getUserPrincipal().getName());
			link.setUser(user);
			linkService.saveLink(link);
			redirectAttributes.addAttribute("linkId", link.getLinkId())
								.addFlashAttribute("success", true);
			return "redirect:/links/link/{linkId}";
		}
	}	
	
	@Secured({"ROLE_ADMIN"})
	@PostMapping("/link/comments")
	public String saveNewComment(@Valid Comment comment, Model model, BindingResult bindingResult, RedirectAttributes attributes) {		
		
		if(bindingResult.hasErrors()) {
			LOGGER.warn("Validation failed of comment: {}", comment.toString());
			model.addAttribute("newLink", comment);
			return "link/submit";
		} else {
			User currentUser = (User) userDetailsService.loadUserByUsername(SecurityContextHolder.getContext()	
																								.getAuthentication().getName());
			comment.setUser((User) model.asMap().get("user"));
			LOGGER.info("Saved comment {} for  link: {} from {}", comment.toString(), comment.getLink().getLinkId(), currentUser.getEmail());
			attributes.addFlashAttribute("success", true);
			commentRepository.saveAndFlush(comment);
			
			return "redirect:/links/link/".concat(comment.getLink().getLinkId().toString());
		}
	}	
}
