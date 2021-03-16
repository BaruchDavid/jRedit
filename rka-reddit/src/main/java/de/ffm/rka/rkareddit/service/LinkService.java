package de.ffm.rka.rkareddit.service;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.Tag;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.repository.LinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * maintenance all business logic for link treating
 * creates basically read transaction
 * Hibernate does some optimizing for read-only entities:
 * It saves execution time by not dirty-checking simple properties or single-ended associations.
 * It saves memory by deleting database snapshots.
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
	 * return all available links
	 */
	public Optional<List<Link>> findAllLinks(){
		List<Link> links = linkRepository.findAll();
		LOGGER.info("Found {} links", links.size());
		return Optional.ofNullable(links);
	}

	/**
	 * return all available links
	 */
	public LinkDTO findLinkBySignature(final String signature) throws ServiceException {
		Link linkModel = findLinkModelWithUser(signature);
		return LinkDTO.mapFullyLinkToDto(linkModel);
	}

	public Link findLinkModelBySignature(final String signature) throws ServiceException {
		return findLinkModelWithUser(signature);
	}

	@Async
	@Transactional(readOnly = false)
	public void createClickedUserLinkHistory(User user, Link link) {
		link.setUsersLinksHistory(new HashSet<>(Collections.singletonList(user)));
		link = linkRepository.save(link);
		LOGGER.info("Link {} and User {} has been saved in history", link.getLinkId(), user.getEmail());
		LOGGER.info("THREAD ASYNC NAME: {}", Thread.currentThread().getName());
	}

	/**
	 * is package-private
	 * find link object
	 * @param signature for link
	 * @return link
	 * @throws ServiceException for application
	 */
	Link findLinkModelWithUser(final String signature) throws  ServiceException{
		LOGGER.info("FIND LINK WITH SIGNATURE {}", signature);
		final long id = LinkDTO.convertEpochSecToId(signature);
		return linkRepository.findLinkWithUserByLinkId(id)
				.orElseThrow(() ->new ServiceException("not found"));
	}

	public Link findLinkWithTags(final String signature) {
		final long id = LinkDTO.convertEpochSecToId(signature);
		return  linkRepository.findTagsForLink(id);

	}

	public Optional<Set<Link>> findLinksWithCommentsByLinkIds(Set<Long> linkIds){
		return Optional.ofNullable(linkRepository.findLinksWithComments(linkIds));
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
		linkDto = LinkDTO.mapFullyLinkToDto(newLink);
		return Optional.of(linkDto)
						.orElse(LinkDTO.builder()
								.title("not available")
								.url("localhost:5550/jReditt/")
								.build());
	}

	public long countLinkByUser(User user) {
		return linkRepository.countByUser(user);
	}

	/**
	 * retrieve links as pageable and then pageable links retrieve
	 * their comments, otherwise each link retrieves their comments
	 * or has LazyInitialization during mapping to linkDto
	 * Query-result rises from two to three
	 * @param pageable, number for one page
	 * @return linkDto objects as PageImpl
	 */
	public Page<LinkDTO> fetchAllLinksWithUsers(Pageable pageable){
		Page<Link> ln = linkRepository.findAll(pageable);
		Set<Link> linksWithComments = this.findLinksWithCommentsByLinkIds(getLinkIds(new HashSet<>(ln.getContent())))
											.orElseGet(Collections::emptySet);
		List<LinkDTO> links = linksWithComments.stream()
								.map(LinkDTO::mapFullyLinkToDto)
								.collect(Collectors.toList());
		return new PageImpl<>(links, pageable, ln.getTotalElements());
	}

	Set<Long> getLinkIds(Set<Link> links) {
		return links.stream()
					.map(Link::getLinkId)
					.collect(Collectors.toSet());
	}

	public List<Link> fetchAllLinksNoDTOWithUsersCommentsVotes(Pageable pageable){
		Page<Link> ln = linkRepository.findAll(pageable);
		return ln.getContent();
	}

	public Set<Link> findLinksWithOwnComments(String username) {
		return linkRepository.findLinkWithUserComments(username);
	}
}
