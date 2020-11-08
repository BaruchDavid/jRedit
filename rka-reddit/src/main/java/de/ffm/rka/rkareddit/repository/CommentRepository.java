package de.ffm.rka.rkareddit.repository;

import de.ffm.rka.rkareddit.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
