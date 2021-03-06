package de.ffm.rka.rkareddit.rest.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.Vote;
import de.ffm.rka.rkareddit.repository.LinkRepository;
import de.ffm.rka.rkareddit.repository.TagRepository;
import de.ffm.rka.rkareddit.repository.VoteRepository;

@RestController
public class TagController {

	private static final Logger LOGGER = LoggerFactory.getLogger(TagController.class);
	
	@Autowired
	private TagRepository tagRepository;
	
	@Autowired
	private LinkRepository linkRepository;
	
	
	/**
	 * 
	 * @param linkId which will be voted
	 * @param direction down or top
	 * @param voteCount is sum of votes
	 * @return new sum of votes
	 * @author Roman
	 */
	@Secured({"ROLE_USER"})
	@GetMapping("/tag/link/{linkId}/direction/{direction}/votecount/{voteCount}")
	public int saveTag(@PathVariable Long  linkId, 
					@PathVariable short direction, 
					@PathVariable int voteCount, Model model, HttpServletRequest req) {
		LOGGER.info("USER AUTHETICATION DETAILS FOR VOTE {}", req.getUserPrincipal());
		try {
			Optional<Link> link = null;//tagRepository.findAll();
//			if(link.isPresent()) {
//				Link linkObj = link.get();
//				Vote vote = new Vote(linkObj, direction);
//				int voteCounter = voteCount + direction;
//				linkObj.setVoteCount(voteCounter);
//				tagRepository.saveAndFlush(vote);	
				return 1;
		} catch (RuntimeException e) {
			LOGGER.error("NO PERMISSION FOR USER TO VOTE", e);
		}
		return voteCount;
	}
}
