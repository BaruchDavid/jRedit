package de.ffm.rka.rkareddit.controller;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.CommentDTO;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.domain.dto.TagDTO;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.service.LinkService;
import de.ffm.rka.rkareddit.service.TagServiceImpl;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Controller
public class LinkController {
	private final LinkService linkService;
	private static final String NEW_LINK = "newLink";
	private static final String SUBMIT_LINK = "link/submit";
	private static final String SUCCESS = "success";
	private static final String ERROR_MESSAGE = "error_message";
	private static final String USER_DTO = "userDto";
	private static final String REDIRECT_MESSAGE = "redirectMessage";
	private final TagServiceImpl tagService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LinkController.class);


	public LinkController(LinkService linkService,
						  TagServiceImpl tagService) {
		this.linkService = linkService;
		this.tagService = tagService;
	}

	/**
	 * load links with all there attributes alike votes, comments, user for each link
	 * @param page contains a page-number, page-size and sorting
	 */
	@GetMapping({"/","", "/links/", "/links"})
	public String links(@PageableDefault(size = 6, direction = Sort.Direction.DESC, sort = "creationDate") Pageable page,
						@RequestParam(name = "searchTag", required = false, defaultValue = "") String searchTag,
						@AuthenticationPrincipal UserDetails user, Model model) {
		Page<LinkDTO> links = linkService.fetchLinksWithUsers(page, searchTag);
		LOGGER.info("{} Links has been found for start page", links.getContent().size());
		List<Integer> totalPages = IntStream.rangeClosed(1, links.getTotalPages())
											.boxed()
											.collect(Collectors.toList());
		if(user != null) {
			model.addAttribute(USER_DTO, UserDTO.mapUserToUserDto((User)user));
		}
		if (model.containsAttribute(SUCCESS)) {
			model.addAttribute(SUCCESS, true);
			model.addAttribute(REDIRECT_MESSAGE, model.asMap().get(REDIRECT_MESSAGE));
			model.addAttribute(USER_DTO, UserDTO.builder().build());
		}
		
		model.addAttribute("links",links);
 		model.addAttribute("pageNumbers",totalPages);	
		return "link/link_list";
	}

	/**
	 *
	 * @param model save data for view
	 * @param signature for linkDTO
	 * @param userDetails current user
	 * @param response set status
	 * @return either link view in success or links-overview
	 */
	@GetMapping("/links/link/{signature}")
	public String link(Model model, @PathVariable String signature,
					   @AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response) throws ServiceException {

		final LinkDTO linkDTO = linkService.fetchLinkWithComments(signature);
		Optional.ofNullable(userDetails).ifPresent(loggedUser -> {
			User userModel = (User)userDetails;
			linkService.createClickedUserLinkHistory(userModel, linkDTO);
			model.addAttribute(USER_DTO, UserDTO.mapUserToUserDto(userModel));
		});

		model.addAttribute("linkDto",linkDTO);
		model.addAttribute("commentDto", CommentDTO.builder()
				.lSig(linkDTO.getLinkSignature())
				.build());

		if (model.containsAttribute(SUCCESS)) {
			model.addAttribute(SUCCESS, model.containsAttribute(SUCCESS));
		} else if(model.containsAttribute(ERROR_MESSAGE)) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			model.addAttribute(ERROR_MESSAGE, model.asMap().get(ERROR_MESSAGE));
		}
		return "link/link_view";
	}

	/**
	 * view for entering data for new link
	 * @param user
	 * @param model
	 * @return
	 */
	@GetMapping("/links/link")
	public String newLink(@AuthenticationPrincipal UserDetails user, Model model) {
		model.addAttribute(USER_DTO, UserDTO.mapUserToUserDto((User)user));
		LinkDTO linkDTO = new LinkDTO();
		for(int i=0; i<4; ++i) {
			linkDTO.getTags().add(TagDTO.builder().tagName("").build());
		}
		model.addAttribute(NEW_LINK, linkDTO);
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
						  RedirectAttributes redirectAttributes) throws ServiceException {
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
