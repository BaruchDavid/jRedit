package de.ffm.rka.rkareddit.repository;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LinkRepository extends JpaRepository<Link, Long> {
	
	/**
	 * namedQuery, SpringData introduces count-statement
	 */
	long countByUser(User usr);

	/**
	 * find all comments for each user
	 */
	@Query(value = "SELECT l " +
		   "FROM Link l " +
		   "INNER JOIN FETCH l.user ORDER BY l.linkId DESC",
		   countQuery = "SELECT COUNT(l) FROM Link l INNER JOIN l.user")
	Page<Link> findAll(Pageable pageable);

    Optional<Link> findByLinkId(long id);

    @Query("SELECT link FROM Link link WHERE link.linkId =:id")
    Optional<Link> findLinkById(@Param("id") long id);

    @Query("SELECT l "
    		+ "FROM Link l "
    		+ "INNER JOIN FETCH l.tags "
    		+ "WHERE l.linkId =:id ")
	Link findTagsForLink(@Param("id") long id);

	@Query("SELECT l "
			+ "FROM Link l "
			+ "INNER JOIN FETCH l.user "
			+ "WHERE l.linkId =:id ")
	Optional<Link> findLinkWithUserByLinkId(long id);

	@Query("SELECT l "
			+ "FROM Link l "
			+ "LEFT OUTER JOIN FETCH l.comments "
			+ "WHERE l.linkId IN (:linkIds) ")
	Set<Link> findLinksWithComments(@Param("linkIds") List<Long> linkIds);
}
