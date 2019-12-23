package de.ffm.rka.rkareddit.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import de.ffm.rka.rkareddit.domain.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

	List <Tag> findByTag(@Param("tag") String tag);

}
