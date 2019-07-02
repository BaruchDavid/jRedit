package de.ffm.rka.rkareddit.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;

public interface LinkRepository extends JpaRepository<Link, Long> {
	
	/**
	 * namedQuery, SpringData introducts count-statement
	 */
	long countByUser(User usr);
	
	@Query("SELECT link "
			+ "FROM Link link "
			+ "LEFT JOIN FETCH link.comments ")
	Set<Link> fetchAllLinksWithComments();
		
}
