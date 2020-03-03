package de.ffm.rka.rkareddit.rest.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.Tag;
import de.ffm.rka.rkareddit.service.LinkService;
import de.ffm.rka.rkareddit.service.TagServiceImpl;
import de.ffm.rka.rkareddit.service.UserService;
import de.ffm.rka.rkareddit.vo.TagVO;


@RestController
@RequestMapping("/tags")
public class TagController {
	private static final Logger LOGGER = LoggerFactory.getLogger(TagController.class);
	
	private UserService userService;
	private TagServiceImpl tagService;
	private LinkService linkService;

	public TagController(UserService userService, TagServiceImpl tagServiceImpl, LinkService linkService) {
		this.userService = userService;
		this.tagService = tagServiceImpl;
		this.linkService = linkService;
	}

	/**
	 * method for login and logout
	 * during logout, request parameter contains 'logout' param
	 * after session-timeout, you will be redirected to login again
	 * @param request
	 * @return view for login / logout
	 */
	@Secured({"ROLE_ADMIN"})
	@PostMapping(value ="/tag/create", consumes = MediaType.ALL_VALUE)
	@ResponseBody
	public TagVO saveNewTag(@RequestBody String tag, @AuthenticationPrincipal UserDetails user, Model model) {		
		Tag nTag = new Tag(tag.substring(0,tag.indexOf("=")));
		Optional<Tag> availibleTag = tagService.findTagOnName(nTag.getName());
		if (availibleTag.isPresent()) {
			return new TagVO(availibleTag.get().getName(), availibleTag.get().getTagId());
		} else {
			long id = tagService.saveTag(nTag);
			LOGGER.info("NEW TAG: {}", nTag.toString());
			return new TagVO(nTag.getName(), nTag.getTagId());
		}
		
//		Überprüfung im Validator einbauen
//		if(bindingResult.hasErrors()) {			
//			if(userService.lockUser(user.getUsername())) {
//				LOGGER.info("LOCK USER {}, validation failed of tag: {}", user.getUsername(),tag.toString());
//			} else {
//				LOGGER.info("USER {} IS NOT IN THE SYSTEM ANYMORE, All links and tags has been deleted from him: {}", 
//						user.getUsername(),tag.toString());
//			}
//		} else {			

//		}
	}	
	
	@Secured({"ROLE_ADMIN"})
	@DeleteMapping(value ="/tag/deleteTag/{tagId}")
	@ResponseBody
	public String deleteTagWithoutRelation(@PathVariable long tagId, @AuthenticationPrincipal UserDetails user, Model model) {		
		String deletedTagId = "";
		Optional<Tag> tag = tagService.selectTag(tagId);
		if (tag.isPresent()) {
			if(tag.get().getLinks().size()==0) {
				deletedTagId = String.valueOf(tag.get().getTagId());
				tagService.deleteTagWithoutRelation(tag.get().getTagId());
				LOGGER.info("DELETE TAG WITHOUT RELATION: {}", deletedTagId);
			}
		}
		return deletedTagId;	
	}
}

