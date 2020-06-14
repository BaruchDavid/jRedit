package de.ffm.rka.rkareddit.service;

import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.repository.CommentRepository;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * maintance all business logik for link treating
 * creates basically read transaction 
 * @author RKA
 *
 */
@Service
@Transactional(readOnly = true)
public class CommentService {

	private final CommentRepository commentRepository;
	private final UserDetailsServiceImpl userDetailsService;

	public CommentService(CommentRepository commentReptory, UserDetailsServiceImpl userDetailsService) {
		this.commentRepository = commentReptory;
		this.userDetailsService = userDetailsService;
	}
	
	/**
	 * counts all comments undepends on links or posts
	 */
	public long countAllByUser(User user) {
		return commentRepository.countByUser(user);
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
	public void saveNewComment(final String userName, Comment comment) {
		comment.setUser((User) userDetailsService.loadUserByUsername(userName));
		commentRepository.saveAndFlush(comment);
	}
}
