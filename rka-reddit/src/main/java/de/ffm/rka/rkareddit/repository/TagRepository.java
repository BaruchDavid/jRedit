package de.ffm.rka.rkareddit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import de.ffm.rka.rkareddit.domain.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

	@Query(value = "SELECT name FROM Tag WHERE UPPER(name) LIKE UPPER(CONCAT('%', :tagName,'%'))")
	List<String> findTagByName(@Param("tagName") String tagName);
	//List<String> findByNameContainingIgnoreCase(String tagName);
}
