package de.ffm.rka.rkareddit.repository;


import de.ffm.rka.rkareddit.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;


public interface VoteRepository extends JpaRepository<Vote, Long> {

	
    Vote findByVoteId(@Param("voteId") long id);
}
