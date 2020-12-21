package de.ffm.rka.rkareddit.service;

import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.CommentDTO;
import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.repository.CommentRepository;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * maintenance all business logic for link treating
 * creates basically read transaction 
 * @author RKA
 *
 */
@Service
@Transactional(readOnly = true)
public class CommentService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommentService.class);
	private final CommentRepository commentRepository;
	private final UserDetailsServiceImpl userDetailsService;
	private final LinkService linkService;

	public CommentService(CommentRepository commentRepository, UserDetailsServiceImpl userDetailsService,
						  LinkService linkService) {
		this.commentRepository = commentRepository;
		this.userDetailsService = userDetailsService;
		this.linkService = linkService;
	}
	
	public String getElapsedTimeFromComment(Comment com) {
		return commentRepository.findById(com.getCommentId())
								.map(Comment::getElapsedTime)
								.orElse("No creation time available");
	}

	/**
	 * @param userName who creates comment
	 * @param comment content
	 */
	@Transactional(readOnly = false)
	public CommentDTO saveNewComment(final String userName, CommentDTO comment) throws ServiceException {
		comment.setUser((User) userDetailsService.loadUserByUsername(userName));
		Comment cm = CommentDTO.getMapDtoToComment(comment);
		Link suitableLink = getSuitableLink(comment.getLSig());
		suitableLink.setCommentCount(suitableLink.getCommentCount()+1);
		cm.setLink(suitableLink);
		comment = CommentDTO.getCommentToCommentDto(commentRepository.save(cm));
		LOGGER.info("{} SAVED COMMENT FOR LINK {}", comment, comment.getLSig());
		return comment;
	}

	private Link getSuitableLink(String linkSignature) throws ServiceException {
		return linkService.findLinkModelWithUser(linkSignature);
	}


	public List<Comment> retrieveCommentsForLink(Long linkId) {
		LOGGER.info("THREAD NAME: {}", Thread.currentThread().getName());
		return commentRepository.findAllCommentsWithLinkId(linkId);
	}
}
