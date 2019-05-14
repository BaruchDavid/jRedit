package de.ffm.rka.rkareddit.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.ocpsoft.prettytime.PrettyTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.repository.LinkRepository;

@Controller
@RequestMapping("/links")
public class LinkController {

	private LinkRepository linkRepository;
	private static final Logger LOGGER = LoggerFactory.getLogger(LinkController.class);

	public LinkController(LinkRepository linkRepository) {
		this.linkRepository = linkRepository;
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
			model.addAttribute("link",link.get());
			model.addAttribute("success",model.containsAttribute("success"));
			return "link/link_view";
		}else {
			return "redirect:/links";
		}
	}
	
	@GetMapping("/link/create")
	public String createNewLink(Model model) {
		model.addAttribute("newLink", new Link());
		return "link/submit";
	}
		
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
	
//	@PutMapping("/link/{linkId}")
//	public Link update(@ModelAttribute Link link) {		
//		return linkRepository.saveAndFlush(link);
//	}
//	
//	@DeleteMapping("/{linkId}")
//	public void delete(Model model, @PathVariable Long linkId) {		
//		linkRepository.deleteById(linkId);
//	}
}
