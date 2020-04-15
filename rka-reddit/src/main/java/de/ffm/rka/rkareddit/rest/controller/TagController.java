package de.ffm.rka.rkareddit.rest.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import de.ffm.rka.rkareddit.domain.Tag;
import de.ffm.rka.rkareddit.service.TagServiceImpl;

import de.ffm.rka.rkareddit.vo.TagVO;


@RestController
@RequestMapping("/tags")
public class TagController {
	private static final Logger LOGGER = LoggerFactory.getLogger(TagController.class);
	
	private TagServiceImpl tagService;


	public TagController(TagServiceImpl tagServiceImpl) {
		this.tagService = tagServiceImpl;
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
		Tag nTag = Tag.builder()
					  .tagId(0l)
					  .tagName(tag.substring(0,tag.indexOf('=')))
					  .build();
		
		Optional<Tag> availibleTag = tagService.findTagOnName(nTag.getTagName());
		if (availibleTag.isPresent()) {
			return new TagVO(availibleTag.get().getTagName(), availibleTag.get().getTagId());
		} else {
			LOGGER.info("AUTOCOMPLETE NO RESULT FOR: {}", tag);
			return new TagVO(nTag.getTagName(), nTag.getTagId());
		}
	}	
	
	@Secured({"ROLE_ADMIN"})
	@DeleteMapping(value ="/tag/deleteTag/{tagId}")
	@ResponseBody
	public String deleteTagWithoutRelation(@PathVariable long tagId, @AuthenticationPrincipal UserDetails user, Model model) {		
		String deletedTagId = "";
		Optional<Tag> tag = tagService.selectTagWithLinks(tagId);
		if (tag.isPresent()
			&& tag.get().getLinks().isEmpty()) {
			deletedTagId = String.valueOf(tag.get().getTagId());
			tagService.deleteTagWithoutRelation(tag.get());
			LOGGER.info("DELETE TAG WITHOUT RELATION: {}", deletedTagId);
		} 
		return deletedTagId;	
	}
}

