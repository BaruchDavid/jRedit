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
 * maintance all business logik for link treating
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

	public CommentService(CommentRepository commentReptory, UserDetailsServiceImpl userDetailsService,
						  LinkService linkService) {
		this.commentRepository = commentReptory;
		this.userDetailsService = userDetailsService;
		this.linkService = linkService;
	}
	
	public String getElapsedTimeFromComment(Comment com) {
		return commentRepository.findById(com.getCommentId())
								.map(Comment::getElapsedTime)
								.orElse("No creation time availible");
	}

	/**
	 * @param userName who creates comment
	 * @param comment content
	 */
	public CommentDTO saveNewComment(final String userName, CommentDTO comment) throws ServiceException {
		comment.setUser((User) userDetailsService.loadUserByUsername(userName));
		Comment cm = CommentDTO.getMapDtoToComment(comment);
		cm.setLink(getSuitableLink(comment.getLSig()));
		comment = CommentDTO.getCommentToCommentDto(commentRepository.saveAndFlush(cm));
		LOGGER.info("{} SAVED COMMENT FOR LINK {}", comment, comment.getLSig());
		return comment;
	}

	private Link getSuitableLink(String linkSignatur) throws ServiceException {
		return linkService.findLinkModelWithUser(linkSignatur);
	}


	public List<Comment> retriveCommentsForLink(Long linkId) {
		return commentRepository.findAllCommentsWithLinkId(linkId);
	}
}
