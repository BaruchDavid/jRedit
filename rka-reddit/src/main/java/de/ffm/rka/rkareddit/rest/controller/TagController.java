package de.ffm.rka.rkareddit.rest.controller;

import de.ffm.rka.rkareddit.domain.Tag;
import de.ffm.rka.rkareddit.service.TagServiceImpl;
import de.ffm.rka.rkareddit.vo.TagVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


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
	 * @return view for login / logout
	 */
	@PostMapping(value ="/tag/create", consumes = MediaType.ALL_VALUE)
	@ResponseBody
	public TagVO newTag(@RequestBody String tag) {
		long id = 0;
		Tag nTag = Tag.builder()
					  .tagId(id)
					  .tagName(tag.substring(0,tag.indexOf('=')))
					  .build();
		
		Optional<Tag> availableTag = tagService.findTagOnName(nTag.getTagName());
		if (availableTag.isPresent()) {
			return new TagVO(availableTag.get().getTagName(), availableTag.get().getTagId());
		} else {
			LOGGER.info("AUTOCOMPLETE NO RESULT FOR: {}", tag);
			return new TagVO(nTag.getTagName(), nTag.getTagId());
		}
	}	
	
	@DeleteMapping(value ="/tag/deleteTag/{tagId}")
	@ResponseBody
	public String tagWithoutRelation(@PathVariable long tagId) {
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

