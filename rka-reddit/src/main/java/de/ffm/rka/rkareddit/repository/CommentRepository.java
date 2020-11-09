package de.ffm.rka.rkareddit.repository;

import de.ffm.rka.rkareddit.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "SELECT comment " +
            "FROM Comment comment " +
            "INNER JOIN FETCH comment.link " +
            "WHERE comment.link.linkId =:linkId")
    List<Comment> findAllCommentsWithLinkId(@Param("linkId") Long linkId);
}
