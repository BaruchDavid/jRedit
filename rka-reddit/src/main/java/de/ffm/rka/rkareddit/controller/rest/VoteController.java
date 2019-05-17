package de.ffm.rka.rkareddit.controller.rest;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.Vote;
import de.ffm.rka.rkareddit.repository.LinkRepository;
import de.ffm.rka.rkareddit.repository.VoteRepository;

@RestController
public class VoteController {

	@Autowired
	private VoteRepository voteRepository;
	
	@Autowired
	private LinkRepository linkRepository;
	
	@GetMapping("/vote/link/{linkId}/direction/{direction}/voteCount/{voteCount}")
	public int vote(@PathVariable Long  linkId, 
					@PathVariable short direction, 
					@PathVariable int voteCount, Model model) {
		
		Optional<Link> link = linkRepository.findById(linkId);
		if(link.isPresent()) {
			Link linkObj = link.get();
			Vote vote = new Vote(linkObj, direction);
			int voteCounter = voteCount + direction;
			linkObj.setVoteCount(voteCounter);
			voteRepository.saveAndFlush(vote);
			linkRepository.saveAndFlush(linkObj); //todo schauen, ob bei bidirectionaler beziehung man noch den link
												//speichern muss, wenn vote vorher gespeichert wurde
			return voteCounter;
		}
		return voteCount;
	}
}
