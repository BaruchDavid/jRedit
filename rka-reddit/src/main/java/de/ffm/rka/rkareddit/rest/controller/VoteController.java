package de.ffm.rka.rkareddit.rest.controller;

import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.service.VoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@RestController
public class VoteController {

	private static final Logger LOGGER = LoggerFactory.getLogger(VoteController.class);
	public static final String ANONYMOUS = "anonymous";
	private final VoteService voteService;

	public VoteController(VoteService voteService) {
		this.voteService = voteService;
	}


	/**
	 * @param lSig which will be voted
	 * @param direction down or top
	 * @param voteCount is sum of votes
	 * @return new sum of votes
	 * @author RKA
	 */
	@GetMapping("/link/{lSig}/vote/direction/{direction}/votecount/{voteCount}")
	public int vote(@PathVariable String  lSig,
					@PathVariable short direction,
					@AuthenticationPrincipal UserDetails userDetails,
					@PathVariable int voteCount, HttpServletRequest req, HttpServletResponse res) throws ServiceException {
		if(userDetails != null && !ANONYMOUS.equals(userDetails.getUsername())){
			int newCount = voteService.saveVote(direction, lSig, voteCount);
			if (voteCount != newCount) {
				return newCount;
			} else {
				res.setStatus(HttpStatus.BAD_REQUEST.value());
				LOGGER.warn("VOTE LOST FOR LINK-SIG {} FROM USER {}", lSig, req.getUserPrincipal());
				return voteCount;
			}
		} else {
			res.setStatus(HttpStatus.UNAUTHORIZED.value());
			LOGGER.warn("VOTE LOST FOR LINK-SIG {} FROM USER {}", lSig, req.getUserPrincipal());
			return voteCount;
		}

	}
}
