package de.ffm.rka.rkareddit.repository;


import de.ffm.rka.rkareddit.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;


public interface VoteRepository extends JpaRepository<Vote, Long> {

	

}
