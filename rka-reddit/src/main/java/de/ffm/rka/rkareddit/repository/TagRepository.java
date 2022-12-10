package de.ffm.rka.rkareddit.repository;

import de.ffm.rka.rkareddit.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query(value = "SELECT DISTINCT tagName FROM Tag WHERE UPPER(tagName) LIKE UPPER(CONCAT('%', :tagName,'%'))")
    List<String> findTagByName(@Param("tagName") String tagName);

    @Query(value = "SELECT tag FROM Tag tag WHERE UPPER(tagName) LIKE UPPER(:tagName)")
    Optional<Tag> findByName(String tagName);

    @Query(value = "SELECT tg.tagId FROM Tag tg JOIN tg.links lk  WHERE tg.tagId =:tagId")
    Optional<String> selectTagIdFromRelation(String tagId);

    @Query(value = "SELECT a FROM Tag a LEFT OUTER JOIN FETCH a.links WHERE a.tagId =:tagId")
    Tag selectTagWithLinks(@Param("tagId") long tagId);
}
