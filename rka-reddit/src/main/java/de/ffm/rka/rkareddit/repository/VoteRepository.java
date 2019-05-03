package de.ffm.rka.rkareddit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.ffm.rka.rkareddit.model.Comment;


public interface VoteRepository extends JpaRepository<Comment, Long> {

}
