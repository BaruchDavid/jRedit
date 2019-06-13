package de.ffm.rka.rkareddit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.User;



public interface CommentRepository extends JpaRepository<Comment, Long> {

	long countByUser(User user);
}
