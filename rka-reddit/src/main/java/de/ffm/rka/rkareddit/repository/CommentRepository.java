package de.ffm.rka.rkareddit.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import de.ffm.rka.rkareddit.model.Vote;


public interface CommentRepository extends JpaRepository<Vote, Long> {

}
