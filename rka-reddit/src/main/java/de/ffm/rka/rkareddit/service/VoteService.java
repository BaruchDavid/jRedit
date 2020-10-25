package de.ffm.rka.rkareddit.service;

import de.ffm.rka.rkareddit.domain.Link;

import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.repository.LinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * maintance all business logik for link treating
 * creates basically read transaction 
 * @author RKA
 *
 */
@Service
@Transactional(readOnly = true)
public class VoteService {
	private static final Logger LOGGER = LoggerFactory.getLogger(VoteService.class);
	private final LinkRepository linkRepository;

	public VoteService(LinkRepository linkRepository) {
		this.linkRepository = linkRepository;
	}

	/**
	 *
	 * @param direction +1 is upvote, -1 is downvote
	 * @param linkSignatur from link
	 * @param voteCount current voteCount
	 * @return empty invalid vote or edited voted
	 * @throws IllegalArgumentException
	 * @throws ServiceException will be thrown, when no link could be found and optional-link is empty
	 */
	@Transactional(readOnly = false)
	public int saveVote(short direction, String linkSignatur, int voteCount) throws IllegalArgumentException, ServiceException  {
		final boolean isValidDirection = direction !=0 && direction < 2 && direction > -2 ? true : false;
		final long linkId = LinkDTO.convertEpochSecToId(linkSignatur);
		Optional<Link> link = linkRepository.findByLinkId(linkId);
		int currentCount = link.map(Link::getVoteCount)
								.orElseThrow(() -> new ServiceException("kein Vote für den Linksignatur: ".concat(linkSignatur)));
		
		if(currentCount == voteCount && isValidDirection) {
			link.get().setVoteCount(currentCount+direction);
			linkRepository.save(link.get());
			return link.get().getVoteCount();
		}

		return voteCount;
	}
}
