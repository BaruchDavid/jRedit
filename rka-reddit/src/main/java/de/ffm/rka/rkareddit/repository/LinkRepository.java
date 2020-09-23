package de.ffm.rka.rkareddit.repository;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {
	
	/**
	 * namedQuery, SpringData introducts count-statement
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
}
