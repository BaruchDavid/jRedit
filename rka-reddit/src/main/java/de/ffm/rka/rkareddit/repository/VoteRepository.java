package de.ffm.rka.rkareddit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.Vote;


public interface VoteRepository extends JpaRepository<Vote, Long> {

	

}
