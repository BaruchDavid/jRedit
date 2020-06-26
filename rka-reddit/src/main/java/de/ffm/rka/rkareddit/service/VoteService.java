package de.ffm.rka.rkareddit.service;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.Vote;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.repository.LinkRepository;
import de.ffm.rka.rkareddit.repository.VoteRepository;
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
	private final VoteRepository voteRepository;
	private final LinkService linkService;
	private final LinkRepository linkRepository;

	public VoteService(VoteRepository voteRepository, LinkService linkService, LinkRepository linkRepository) {
		this.voteRepository = voteRepository;
		this.linkService = linkService;
		this.linkRepository = linkRepository;
	}

	/**
	 *
	 * @param direction +1 is upvote, -1 is downvote
	 * @param linkSignatur from link
	 * @param voteCount current voteCount
	 * @return empty invalid vote or edited voted
	 * @throws IllegalArgumentException
	 */
	@Transactional(readOnly = false)
	public Vote saveVote(short direction, String linkSignatur, int voteCount) throws IllegalArgumentException  {
		final boolean isValidDirection = direction !=0 && direction < 2 && direction > -2 ? true : false;
		final long linkId = LinkDTO.convertEpochSecToId(linkSignatur);
		Optional<Link> link = linkRepository.findByLinkId(linkId);
		if(link.isPresent() && isValidDirection){
			if(link.get().getVoteCount() != voteCount){
				return new Vote();
			}
			link.map(lnk -> {
							lnk.setVoteCount(lnk.getVoteCount() + direction);
							return lnk;
			});
			Vote newVote = Vote.builder()
						.direction(direction)
						.link(link.get())
						.build();
			return Optional.ofNullable(voteRepository.save(newVote))
							.orElse(Vote.builder().build());

		} else {
			return new Vote();
		}

	}
}
