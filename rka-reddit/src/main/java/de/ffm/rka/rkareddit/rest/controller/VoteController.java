package de.ffm.rka.rkareddit.rest.controller;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.Vote;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.service.LinkService;
import de.ffm.rka.rkareddit.service.VoteService;
import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RestController
public class VoteController {

	private static final Logger LOGGER = LoggerFactory.getLogger(VoteController.class);

	private final VoteService voteService;

	public VoteController(VoteService voteService) {
		this.voteService = voteService;
	}


	/**
	 * 
	 * @param lSig which will be voted
	 * @param direction down or top
	 * @param voteCount is sum of votes
	 * @return new sum of votes
	 * @author Roman
	 */
	@GetMapping("/vote/link/{lSig}/direction/{direction}/votecount/{voteCount}")
	public int vote(@PathVariable String  lSig,
					@PathVariable short direction, 
					@PathVariable int voteCount, HttpServletRequest req, HttpServletResponse res) throws ServiceException {

		Vote vote = voteService.saveVote(direction, lSig, voteCount);
		if (vote.getVoteId() != null) {
			return vote.getLink().getVoteCount();
		} else {
			res.setStatus(HttpStatus.SC_BAD_REQUEST);
			LOGGER.warn("VOTE LOST FOR LINK-SIG {} FROM USER {}", lSig, req.getUserPrincipal());
			return voteCount;
		}
	}
}
