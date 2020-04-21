package de.ffm.rka.rkareddit.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.Tag;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.repository.CommentRepository;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;
import de.ffm.rka.rkareddit.service.LinkService;
import de.ffm.rka.rkareddit.service.TagServiceImpl;


@Controller
@RequestMapping("/links")
public class LinkController {
	private LinkService linkService;
	private CommentRepository commentRepository;
	private static final String NEW_LINK = "newLink";
	private static final String SUBMIT_LINK = "link/submit";
	private static final String SUCCESS = "success";
	private static final String ERROR = "error";
	private static final String USER_DTO = "userDto";
	private ModelMapper modelMapper;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private TagServiceImpl tagService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LinkController.class);

	public LinkController(LinkService linkService, CommentRepository commentRepository, ModelMapper modelMapper) {
		this.linkService = linkService;
		this.commentRepository = commentRepository;
		this.modelMapper = modelMapper;
	}

	/**
	 * load links with all there attributes alike votes, comments, user for each link
	 * @param page contains a page-number, page-size and sorting
	 */
	@GetMapping({"/",""})
	public String list(@PageableDefault(size = 6, direction = Sort.Direction.DESC, sort = "linkId") Pageable page,
						@AuthenticationPrincipal UserDetails user, Model model) {
		Page<Link> links = linkService.fetchAllLinksWithUsersCommentsVotes(page);
		LOGGER.info("{} Links has been found", links.getSize());
		List<Integer> totalPages = IntStream.rangeClosed(1, links.getTotalPages())
											.boxed()
											.collect(Collectors.toList());		
		if(user != null) {
			model.addAttribute(USER_DTO, mapUserToUserDto(user.getUsername()));
		}	
		model.addAttribute("links",links);
 		model.addAttribute("pageNumbers",totalPages);	
		return "link/link_list";
	}
	
	
	@GetMapping("link/{linkId}")
	public String read(Model model, @PathVariable Long linkId, @AuthenticationPrincipal UserDetails user, HttpServletResponse response){
		Optional<Link> link = linkService.findLinkByLinkId(linkId);
		if(link.isPresent()) {
			Link currentLink = link.get();
			Comment comment = new Comment();
			comment.setLink(currentLink);	
			if (user != null) {
				model.addAttribute(USER_DTO, mapUserToUserDto(user.getUsername()));
			}			
			model.addAttribute("link",currentLink);
			model.addAttribute("comment",comment);
			
			if (model.containsAttribute(SUCCESS)) {
				model.addAttribute(SUCCESS, model.containsAttribute(SUCCESS));
			} else if(model.containsAttribute(ERROR)) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				model.addAttribute(ERROR, model.containsAttribute(ERROR));
			}
			
			return "link/link_view";
		}else {
			return "redirect:/links";
		}
	}
	
	@GetMapping("/link/create")
	public String newLink(@AuthenticationPrincipal UserDetails user, Model model) {
		model.addAttribute(USER_DTO, mapUserToUserDto(user.getUsername()));		
		Link link = new Link();
		for(int i=0; i<4; ++i) {
			link.addTag(Tag.builder().tagName("").build());
		}
		model.addAttribute(NEW_LINK, link);
		return SUBMIT_LINK;
	}
	
	@PostMapping("/link/create")
	public String newLink(@Valid Link link, @AuthenticationPrincipal UserDetails user, Model model, HttpServletRequest request,
							BindingResult bindingResult, RedirectAttributes redirectAttributes) {		
		
		if(bindingResult.hasErrors()) {
			LOGGER.error("Validation failed of link: {}", link);
			model.addAttribute(NEW_LINK, link);
			return SUBMIT_LINK;
		} else {
			link.setUser((User)userDetailsService.loadUserByUsername(user.getUsername()));
			linkService.saveLink(link);
			redirectAttributes.addAttribute("linkId", link.getLinkId())
								.addFlashAttribute(SUCCESS, true);			
			return "redirect:/links/link/{linkId}";
		}
	}	
	
	@PostMapping(value = "/link/comments")
	public String saveNewComment(@Valid Comment comment, BindingResult bindingResult, 
								RedirectAttributes attributes,Model model,
								@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest req, HttpServletResponse res) {		

		if(bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach(error -> LOGGER.error("VALIDATION ON COMMENT {} : CODES {} MESSAGE: {}", 
															comment, error.getCodes(), error.getDefaultMessage()));	
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			attributes.addFlashAttribute(ERROR, true);
		} else {
			comment.setUser((User) userDetailsService.loadUserByUsername(userDetails.getUsername()));
			attributes.addFlashAttribute(SUCCESS, true);
			commentRepository.saveAndFlush(comment);
		}
		return "redirect:/links/link/".concat(comment.getLink().getLinkId().toString());
	}		
	
	@PostMapping(value = "/link/search")
	@ResponseBody
	public List<String> completeSearch(String search, Model model, HttpServletResponse req) {		
		return tagService.findSuitableTags(search);
	}
		
	private UserDTO mapUserToUserDto(String usrName) {
		User usrObj = Optional.ofNullable((User) userDetailsService.loadUserByUsername(usrName))
								.orElseThrow(()-> new UsernameNotFoundException("user not found"));
		return modelMapper.map(usrObj, UserDTO.class);
	}
}
