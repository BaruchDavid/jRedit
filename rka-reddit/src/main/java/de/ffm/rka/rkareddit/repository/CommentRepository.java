package de.ffm.rka.rkareddit.repository;

import de.ffm.rka.rkareddit.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "SELECT comment " +
            "FROM Comment comment " +
            "INNER JOIN FETCH comment.link " +
            "WHERE comment.link.linkId =:linkId")
    List<Comment> findAllCommentsWithLinkId(@Param("linkId") Long linkId);

    @Query(value = "SELECT comment " +
            "FROM Comment comment " +
            "JOIN FETCH comment.link " +
            "WHERE comment.user.email =:mail")
    Set<Comment> getUserComments(@Param("mail") String username);


    @Query(value = "SELECT comment " +
            "FROM Comment comment " +
            "JOIN FETCH comment.user " +
            "WHERE comment.commentId =:commentId")
    Comment findUserForComment(@Param("commentId") Long commentId);
}
