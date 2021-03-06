package de.ffm.rka.rkareddit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;

public interface LinkRepository extends JpaRepository<Link, Long> {
	
	/**
	 * namedQuery, SpringData introducts count-statement
	 */
	long countByUser(User usr);

	/**
	 * find all comments for each user
	 */
	@Query(value = "select l " +
		   "from Link l " +
		   "inner join fetch l.user",
		   countQuery = "SELECT COUNT(l) FROM Link l INNER JOIN l.user")
	Page<Link> findAll(Pageable pageable);
		
}
