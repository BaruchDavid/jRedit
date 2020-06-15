package de.ffm.rka.rkareddit.controller;

import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.Tag;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.repository.CommentRepository;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;
import de.ffm.rka.rkareddit.service.LinkService;
import de.ffm.rka.rkareddit.service.TagServiceImpl;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Controller
public class LinkController {
	private final LinkService linkService;
	private final CommentRepository commentRepository;
	private static final String NEW_LINK = "newLink";
	private static final String SUBMIT_LINK = "link/submit";
	private static final String SUCCESS = "success";
	private static final String ERROR = "error";
	private static final String USER_DTO = "userDto";
	private final UserDetailsServiceImpl userDetailsService;
	private final TagServiceImpl tagService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LinkController.class);

	public LinkController(LinkService linkService, CommentRepository commentRepository,
						  UserDetailsServiceImpl userDetailsService, TagServiceImpl tagService) {
		this.linkService = linkService;
		this.commentRepository = commentRepository;
		this.userDetailsService = userDetailsService;
		this.tagService = tagService;
	}

	/**
	 * load links with all there attributes alike votes, comments, user for each link
	 * @param page contains a page-number, page-size and sorting
	 */
	@GetMapping({"/","", "/links/", "/links"})
	public String links(@PageableDefault(size = 6, direction = Sort.Direction.DESC, sort = "creationDate") Pageable page,
						@AuthenticationPrincipal UserDetails user, Model model) {
		Page<Link> links = linkService.fetchAllLinksWithUsersCommentsVotes(page);
		LOGGER.info("{} Links has been found", links.getSize());
		List<Integer> totalPages = IntStream.rangeClosed(1, links.getTotalPages())
											.boxed()
											.collect(Collectors.toList());		
		if(user != null) {
			model.addAttribute(USER_DTO, userDetailsService.mapUserToUserDto(user.getUsername()));
		}	
		model.addAttribute("links",links);
 		model.addAttribute("pageNumbers",totalPages);	
		return "link/link_list";
	}

	/**
	 *
	 * @param model
	 * @param signature for linkDTO
	 * @param user current user
	 * @param response set status
	 * @return either link view in success or links-overview
	 */
	@GetMapping("/links/link/{signature}")
	public String link(Model model, @PathVariable String signature,
					   @AuthenticationPrincipal UserDetails user, HttpServletResponse response) throws ServiceException {

		Link link = linkService.findLinkByTitleSignature(signature);
		Comment comment = Comment.builder().link(link).build();
		if (user != null) {
			model.addAttribute(USER_DTO, userDetailsService.mapUserToUserDto(user.getUsername()));
		}
		model.addAttribute("link",link);
		model.addAttribute("comment",comment);

		if (model.containsAttribute(SUCCESS)) {
			model.addAttribute(SUCCESS, model.containsAttribute(SUCCESS));
		} else if(model.containsAttribute(ERROR)) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			model.addAttribute(ERROR, model.containsAttribute(ERROR));
		}
		return "link/link_view";

	}
	
	@GetMapping("/links/link")
	public String newLink(@AuthenticationPrincipal UserDetails user, Model model) {
		model.addAttribute(USER_DTO, userDetailsService.mapUserToUserDto(user.getUsername()));		
		Link link = new Link();
		for(int i=0; i<4; ++i) {
			link.addTag(Tag.builder().tagName("").build());
		}
		model.addAttribute(NEW_LINK, link);
		return SUBMIT_LINK;
	}

	/**
	 * @param link to be saved
	 * @param user authenticated
	 * @param model save current state
	 * @param response set status
	 * @param bindingResult for errors
	 * @param redirectAttributes for message user
	 * @return success ref to new link
	 */
	@PostMapping("/links/link")
	public String newLink(@Validated LinkDTO link, @AuthenticationPrincipal UserDetails user,
						  Model model, HttpServletResponse response, BindingResult bindingResult,
						  RedirectAttributes redirectAttributes) {
		if(bindingResult.hasErrors()) {
			LOGGER.error("Validation failed of link: {}", link);
			model.addAttribute(NEW_LINK, link);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return SUBMIT_LINK;
		} else {
			LinkDTO newLink = linkService.saveLink(user.getUsername(),link);
			redirectAttributes.addFlashAttribute(SUCCESS, true);
			return "redirect:/links/link/".concat(newLink.getLinkSignature());
		}
	}	

	@PostMapping(value = "/links/link/tags")
	@ResponseBody
	public List<String> searchTags(String search) {
		return tagService.findSuitableTags(search);
	}
}
