package de.ffm.rka.rkareddit.rest.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.ffm.rka.rkareddit.domain.Tag;
import de.ffm.rka.rkareddit.service.TagServiceImpl;
import de.ffm.rka.rkareddit.service.UserService;
import de.ffm.rka.rkareddit.vo.TagVO;


@RestController
@RequestMapping("/tags")
public class TagController {
	private static final Logger LOGGER = LoggerFactory.getLogger(TagController.class);
	
	private UserService userService;
	private TagServiceImpl tagService;

	public TagController(UserService userService, TagServiceImpl tagServiceImpl) {
		this.userService = userService;
		tagService = tagServiceImpl;
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
	public TagVO saveNewTag(@RequestBody String tag, @AuthenticationPrincipal UserDetails user, Model model,
							BindingResult bindingResult, RedirectAttributes redirectAttributes) {		
		Tag nTag = new Tag(tag.substring(0,tag.indexOf("=")));
		long id = tagService.saveTag(nTag);
		LOGGER.info("NEW TAG: {}", nTag.toString());
		
		
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
		return new TagVO(nTag.getName(), nTag.getTagId());
	}		
}

