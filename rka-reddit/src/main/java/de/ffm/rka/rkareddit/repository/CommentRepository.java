package de.ffm.rka.rkareddit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.ffm.rka.rkareddit.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
