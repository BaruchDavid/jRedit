package de.ffm.rka.rkareddit.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.ffm.rka.rkareddit.model.Link;
import de.ffm.rka.rkareddit.repository.LinkRepository;

@RestController
@RequestMapping("/links")
public class LinkController {

	private LinkRepository linkRepository;
	
	public LinkController(LinkRepository linkRepository) {
		this.linkRepository = linkRepository;
	}


	@GetMapping("/")
	public List<Link> list(Model model) {		
		return linkRepository.findAll();
	}
	
	
	@PostMapping("/create")
	public Link create(@ModelAttribute Link link) {		
		return linkRepository.save(link);
	}
	
	@GetMapping("/{linkId}")
	public Optional<Link> read(Model model, @PathVariable Long linkId) {		
		return linkRepository.findById(linkId);
	}
	
	@PutMapping("/{linkId}")
	public Link update(@ModelAttribute Link link) {		
		return linkRepository.saveAndFlush(link);
	}
	
	@DeleteMapping("/{linkId}")
	public void delete(Model model, @PathVariable Long linkId) {		
		linkRepository.deleteById(linkId);
	}
}
