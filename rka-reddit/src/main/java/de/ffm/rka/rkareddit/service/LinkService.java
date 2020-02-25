package de.ffm.rka.rkareddit.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.repository.LinkRepository;


/**
 * maintance all business logik for link treating
 * creates basically read transaction 
 * @author RKA
 *
 */
@Service
@Transactional(readOnly = true)
public class LinkService {

	private static final Logger LOGGER = LoggerFactory.getLogger(LinkService.class);
	private LinkRepository linkRepository;
		
	public LinkService( LinkRepository linkRepository) {

		this.linkRepository = linkRepository;
	}

	/**
	 * return all availible links
	 * @autor RKA
	 */
	public List<Link> findAllLinks(){
		List<Link> links = linkRepository.findAll();
		LOGGER.info("Found {} links", links.size());
		return links;
	}
	
	/**
	 * return all availible links
	 * @autor RKA
	 */
	public Optional<Link> findLinkByLinkId(Long linkId){
		LOGGER.info("TRY TO FIND LINK WITH ID {}", linkId);
		Optional<Link> link = linkRepository.findById(linkId);
		link.ifPresent(val -> LOGGER.info("LINK HAS BEEN FOUND WITH ID {}",val.getLinkId().toString()));
		return link;
	}		
	
	/**
	 * create write transactional
	 * @author RKA
	 */
	@Transactional(readOnly = false)
	public Link saveLink(Link link) {
		LOGGER.info("TRY TO SAVE LINK {}", link);
		return Optional.ofNullable(linkRepository.save(link)).orElse(new Link("link not availible", "http://jReditt.com"));
	}


	public long findAllByUser(User user) {
		return linkRepository.countByUser(user);
	}
	
	public Page<Link> fetchAllLinksWithUsersCommentsVotes(Pageable pageable){
		Page<Link> links = linkRepository.findAll(pageable);
		return links;
	}
}
