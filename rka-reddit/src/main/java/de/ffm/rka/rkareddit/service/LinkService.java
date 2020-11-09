package de.ffm.rka.rkareddit.service;

import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.Tag;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.repository.LinkRepository;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
	private final UserDetailsServiceImpl userDetailsService;
	public LinkService(LinkRepository linkRepository, UserDetailsServiceImpl userDetailsService) {
		this.linkRepository = linkRepository;
		this.userDetailsService = userDetailsService;
	}

	/**
	 * return all availible links
	 */
	public List<Link> findAllLinks(){
		List<Link> links = linkRepository.findAll();
		LOGGER.info("Found {} links", links.size());
		return links;
	}
	
	/**
	 * return all availible links
	 */
	public LinkDTO findLinkBySignature(final String signature) throws ServiceException {
		Link linkModel = findLinkModelWithUser(signature);
		return LinkDTO.getMapLinkToDto(linkModel);
	}

	public Link findLinkModelBySignature(final String signature) throws ServiceException {
		return findLinkModelWithUser(signature);
	}

	/**
	 * is package-private
	 * find link object
	 * @param signature
	 * @return
	 * @throws ServiceException
	 */
	Link findLinkModelWithUser(final String signature) throws  ServiceException{
		LOGGER.info("FIND LINK WITH SIGNATUR {}", signature);
		final long id = LinkDTO.convertEpochSecToId(signature);
		return linkRepository.findLinkWithUserByLinkId(id)
				.orElseThrow(() ->new ServiceException("not found"));
	}
	
	public Link findLinkWithTags(final String signature) {
		final long id = LinkDTO.convertEpochSecToId(signature);
		return  linkRepository.findTagsForLink(id);
		
	}

	/**
	 * create write transactional
	 * @author RKA
	 */
	@Transactional(readOnly = false)
	public LinkDTO saveLink(final String username, LinkDTO linkDto) {

		Link link = LinkDTO.getMapDtoToLink(linkDto);
		link.setUser((User)userDetailsService.loadUserByUsername(username));
		List<Tag> tags = new ArrayList<>();
		linkDto.getTags().stream()
					  .filter( tag -> tag.getTagName().isEmpty())
					  .forEach(tags::add);
		link.getTags().removeAll(tags);		
		link.getTags().forEach(tag -> tag.getLinks().add(link));
		LOGGER.debug("TRY TO SAVE LINK {}", link);
		Link newLink = linkRepository.save(link);
		LOGGER.info("LINK SAVED {}", newLink);
		linkDto = LinkDTO.getMapLinkToDto(newLink);
		return Optional.of(linkDto)
						.orElse(LinkDTO.builder()
								.title("not availible")
								.url("localhost:5550/jReditt/")
								.build());
	}

	public long countLinkByUser(User user) {
		return linkRepository.countByUser(user);
	}

	/**
	 * retrieve link objects and map to linkDTO
	 * @param pageable, number for one page
	 * @return linkDto objects as PageImpl
	 */
	public Page<LinkDTO> fetchAllLinksWithUsers(Pageable pageable){
		Page<Link> ln = linkRepository.findAll(pageable);		
		List<LinkDTO> links = ln.stream().map(link -> LinkDTO.getMapLinkToDto(link))
								.collect(Collectors.toList());
		return new PageImpl<>(links, pageable, ln.getTotalElements());
	}
	
	public List<Link> fetchAllLinksNoDTOWithUsersCommentsVotes(Pageable pageable){
		Page<Link> ln = linkRepository.findAll(pageable);
		List<Comment> comments = ln.getContent().get(0).getComments();
		comments.size();
		return ln.getContent();
	}

	/**
	 * save link which has been click for authenticated user
	 */
	@Transactional(readOnly = false)
	public void saveLinkHistory(Link link, User user) {
		link = linkRepository.saveAndFlush(link);
		LOGGER.info("Link {} and User {} has been saved in history", link.getLinkId(), user.getEmail());
	}
	
}
