package de.ffm.rka.rkareddit.service;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.exception.IllegalVoteException;
import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.repository.LinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * maintenance all business logicW for link treating
 * creates basically read transaction
 *
 * @author RKA
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
     * @param direction     +1 is upvote, -1 is down vote
     * @param linkSignature from link
     * @param voteCount     current voteCount
     * @return empty invalid vote or edited voted
     * @throws IllegalArgumentException on trying send illegal vote
     * @throws ServiceException         will be thrown, when no link could be found and optional-link is empty
     */
    @Transactional(readOnly = false)
    public int saveVote(short direction, String linkSignature, int voteCount) throws ServiceException {
        LOGGER.info("VOTING FOR Link {} WITH COUNT {}", linkSignature, voteCount);
        final long linkId = LinkDTO.convertEpochSecToLinkId(linkSignature);
        Optional<Link> link = linkRepository.findByLinkId(linkId);
        Link checkedLink = link.orElseThrow(() -> new ServiceException("LINK NOT FOUND with SIGNATURE: "
																		+ "linkSignature" + " and ID: " + linkId));
        validateVote(direction);
        if (checkedLink.getVoteCount() == voteCount) {

            checkedLink.setVoteCount(checkedLink.getVoteCount() + direction);
            linkRepository.save(checkedLink);
            return checkedLink.getVoteCount();
        }
        return voteCount;
    }

    private boolean validateVote(short direction) {
        LOGGER.info("Start validate vote with direction {}", direction);
        final boolean isValidDirection = direction != 0 && direction < 2 && direction > -2;
        if (!isValidDirection) {
            throw new IllegalVoteException("vote is illegal");
        } else {
            return true;
        }
    }
}
