package de.ffm.rka.rkareddit.service;

import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.Tag;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.repository.LinkRepository;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


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
	private final LinkRepository linkRepository;
	private final ModelMapper modelMapper;
	private final UserDetailsServiceImpl userDetailsService;
	public LinkService(LinkRepository linkRepository, ModelMapper modelMapper, UserDetailsServiceImpl userDetailsService) {
		this.linkRepository = linkRepository;
		this.modelMapper = modelMapper;
		this.userDetailsService = userDetailsService;
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
	public Link findLinkByTitleSignature(final String signature) throws ServiceException {
		LOGGER.info("FIND LINK WITH SIGNATUR {}", signature);
		final long id = convertEpochSecToId(signature);
		return linkRepository.findById(id)
									.orElseThrow(() ->new ServiceException("not found"));
	}		
	
	/**
	 * create write transactional
	 * @author RKA
	 */
	@Transactional(readOnly = false)
	public LinkDTO saveLink(final String username, LinkDTO linkDto) {

		Link link = modelMapper.map(linkDto, Link.class);
		link.setUser((User)userDetailsService.loadUserByUsername(username));
		List<Tag> tags = new ArrayList<>();
		linkDto.getTags().stream()
					  .filter(tag -> tag.getTagName().isEmpty())
					  .forEach(tags::add);
		link.getTags().removeAll(tags);		
		link.getTags().forEach(tag -> tag.getLinks().add(link));
		LOGGER.debug("TRY TO SAVE LINK {}", link);
		Link newLink = linkRepository.save(link);
		LOGGER.info("LINK SAVED {}", newLink);
		String sig = convertLDTtoEpochSec(newLink.getCreationDate()).concat(String.valueOf(newLink.getLinkId()));
		linkDto = modelMapper.map(newLink, LinkDTO.class);
		linkDto.setLinkSignature(sig);
		return Optional.ofNullable(linkDto)
						.orElse(new LinkDTO("link not availible", "http://jReditt.com"));
	}

	/**
	 *
	 * @param time creation date
	 * @return creatation date as milli
	 */
	private String convertLDTtoEpochSec(LocalDateTime time){
		Instant instant = time.atZone(ZoneId.systemDefault()).toInstant();
		return String.valueOf(instant.toEpochMilli());
	}

	/**
	 *
	 * @param timeInSeconds which represend creation date
	 * @return creation date as localdatetime
	 */
	private long convertEpochSecToId(final String timeInSeconds){
		final int timeLatters = 13;
		return Long.valueOf(timeInSeconds.substring(timeLatters, timeInSeconds.length()));
	}


	public long findAllLinksByUser(User user) {
		return linkRepository.countByUser(user);
	}
	
	public Page<Link> fetchAllLinksWithUsersCommentsVotes(Pageable pageable){
		return linkRepository.findAll(pageable);
	}
}
