package de.ffm.rka.rkareddit.controller;


import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
	public String list(HttpServletRequest request, 
						@PageableDefault(size = 6, direction = Sort.Direction.DESC, sort = "linkId") Pageable page,
						Model model) {
		Enumeration<String> sessionAttributeNames = request.getSession().getAttributeNames();
		HttpSession session = request.getSession();
		while(sessionAttributeNames.hasMoreElements()) {
			Optional<String> attributeName = Optional.of(sessionAttributeNames.nextElement());
			attributeName.ifPresent(attr -> {
				LOGGER.debug("Session attribute name: {}", attr);
				Optional<Object> attribute = Optional.of(session.getAttribute(attr));
				LOGGER.debug("Session attribute value: {}", attribute.isPresent()?attribute.get():"no value");
			});
		}
		Page<Link> links = linkService.fetchAllLinksWithUsersCommentsVotes(page);
		List<Integer> totalPages = IntStream.rangeClosed(1, links.getTotalPages())
											.boxed()
											.collect(Collectors.toList());
		LOGGER.info("{} Links has been found", links.getSize());
		
		model.addAttribute("links",links);
		model.addAttribute("pageNumbers",totalPages);
		return "link/link_list";
		 
	}

}
