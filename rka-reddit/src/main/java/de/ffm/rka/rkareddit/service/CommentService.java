package de.ffm.rka.rkareddit.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.repository.CommentRepository;
import de.ffm.rka.rkareddit.repository.LinkRepository;

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
	
}
