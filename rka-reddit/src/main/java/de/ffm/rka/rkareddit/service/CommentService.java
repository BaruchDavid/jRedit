package de.ffm.rka.rkareddit.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.repository.CommentRepository;

/**
 * maintance all business logik for link treating
 * creates basically read transaction 
 * @author RKA
 *
 */
@Service
@Transactional(readOnly = true)
public class CommentService {

	private CommentRepository commentRepository;
	
	public CommentService( CommentRepository commentReptory) {

		this.commentRepository = commentReptory;
	}
	
	/**
	 * counts all comments undepends on links or posts
	 */
	public long countAllByUser(User user) {
		return commentRepository.countByUser(user);
	}
	
	public String getElapsedTimeFromComment(Comment com) {
		Optional<Comment> comment = commentRepository.findById(com.getCommentId());
		return comment.isPresent()?comment.get().getElapsedTime():"No creation time availible";
	}
	
}
