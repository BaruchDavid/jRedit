package de.ffm.rka.rkareddit.rest.controller;

import java.util.Optional;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import de.ffm.rka.rkareddit.domain.Tag;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.service.TagServiceImpl;
import de.ffm.rka.rkareddit.service.UserService;


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
	@PostMapping("/tag/create")
	@ResponseBody
	public boolean saveNewTag(@Valid Tag tag, @AuthenticationPrincipal UserDetails user, Model model,
							BindingResult bindingResult, RedirectAttributes redirectAttributes) {		
		boolean result=false;
		if(bindingResult.hasErrors()) {			
			if(userService.lockUser(user.getUsername())) {
				LOGGER.info("LOCK USER {}, validation failed of tag: {}", user.getUsername(),tag.toString());
			} else {
				LOGGER.info("USER {} IS NOT IN THE SYSTEM ANYMORE, All links and tags has been deleted from him: {}", 
						user.getUsername(),tag.toString());
			}
		} else {
			tagService.saveTag(tag);
			result = true;
		}
		return result;
	}		
}

