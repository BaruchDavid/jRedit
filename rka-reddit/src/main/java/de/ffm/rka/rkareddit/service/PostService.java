package de.ffm.rka.rkareddit.service;

import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.CommentDTO;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.repository.CommentRepository;
import de.ffm.rka.rkareddit.repository.LinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * maintenance all business logic for link treating
 * creates basically read transaction
 * Hibernate does some optimizing for read-only entities:
 * It saves execution time by not dirty-checking simple properties or single-ended associations.
 * It saves memory by deleting database snapshots.
 *
 * @author RKA
 */
@Service
@Transactional(readOnly = true)
public class PostService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostService.class);
    private final LinkRepository linkRepository;

    private final CommentRepository commentRepository;
    private final UserDetailsServiceImpl userDetailsService;

    public PostService(LinkRepository linkRepository,
                       CommentRepository commentRepository, UserDetailsServiceImpl userDetailsService) {
        this.linkRepository = linkRepository;
        this.commentRepository = commentRepository;
        this.userDetailsService = userDetailsService;

    }

    public LinkDTO findLinkWithComments(final String signature) throws ServiceException {
        final Link link = findLinkModelWithUser(signature);
        link.setComments(this.findCommentsForLink(link.getLinkId()));
        final LinkDTO linkDTO = LinkDTO.mapFullyLinkToDto(link);
        orderCommentsOfEachLink(linkDTO);
        return linkDTO;
    }

    @Async
    @Transactional(readOnly = false)
    public void createClickedUserLinkHistory(User user, LinkDTO linkDTO) {
        Link link = LinkDTO.getMapDtoToLink(linkDTO);
        link.setUsersLinksHistory(new HashSet<>(Collections.singletonList(user)));
        link = linkRepository.save(link);
        LOGGER.info("Link {} and User {} has been saved in history", link.getLinkId(), user.getEmail());
        LOGGER.info("CREATE CLICK-HISTORY: THREAD ASYNC NAME: {}", Thread.currentThread().getName());
    }

    /**
     * is package-private
     * find link object
     *
     * @param signature for link
     * @return link
     * @throws ServiceException for application
     */
    Link findLinkModelWithUser(final String signature) throws ServiceException {
        LOGGER.info("FIND LINK WITH SIGNATURE {}", signature);
        final long id = LinkDTO.convertEpochSecToLinkId(signature);
        return linkRepository.findLinkWithUserByLinkId(id)
                .orElseThrow(() -> new ServiceException("not found"));
    }

    public Link findLinkWithTags(final String signature) throws ServiceException {
        final long id = LinkDTO.convertEpochSecToLinkId(signature);
        return linkRepository.findTagsForLink(id);
    }

    public Optional<Set<Link>> findLinksWithCommentsByLinkIds(Set<Long> linkIds) {
        return Optional.ofNullable(linkRepository.findLinksWithComments(linkIds));
    }

    /**
     * create write transactional
     *
     * @author RKA
     */
    @Transactional(readOnly = false)
    public LinkDTO saveLink(final String username, LinkDTO linkDto) {

        Link link = LinkDTO.getMapDtoToLink(linkDto);
        link.setUser((User) userDetailsService.loadUserByUsername(username));
        link.setTags(link.getTags().stream()
                .filter(tag -> StringUtils.hasText(tag.getTagName()))
                .collect(Collectors.toSet()));
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
     *
     * @param pageable, number for one page
     * @return linkDto objects as PageImpl
     */
    public Page<LinkDTO> findLinksWithUsers(Pageable pageable, String searchTag) {

        Page<Link> ln = searchTag.isEmpty() ? linkRepository.findAll(pageable)
                : linkRepository.findLinksOnTag(searchTag, pageable);
        Set<Link> linksWithComments = this.findLinksWithCommentsByLinkIds(getLinkIds(new HashSet<>(ln.getContent())))
                .orElseGet(Collections::emptySet);
        List<LinkDTO> links = linksWithComments.stream()
                .map(LinkDTO::mapFullyLinkToDto)
                .collect(Collectors.toList());
        return new PageImpl<>(links, pageable, ln.getTotalElements());
    }

    // TODO: 03.12.2022 gehört laut SOLID (erster Prinzip: Single Respositiblity)
    //  NICHT HIER REIN. Hier wird nur mit Repositories gearbeitet
    private void orderCommentsOfEachLink(LinkDTO linkDTO) {

        final Set<CommentDTO> comments = linkDTO.getCommentDTOS().stream()
                .sorted(Comparator.comparing(CommentDTO::getCreationDate))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        linkDTO.setCommentDTOS(comments);

    }

    Set<Long> getLinkIds(Set<Link> links) {
        return links.stream()
                .map(Link::getLinkId)
                .collect(Collectors.toSet());
    }

    public String findCommentWithElapsedtime(Comment com) {
        return commentRepository.findById(com.getCommentId())
                .map(Comment::getElapsedTime)
                .orElse("No creation time available");
    }

    /**
     * @param userName who creates comment
     * @param comment  content
     */
    @Transactional(readOnly = false)
    public CommentDTO saveNewComment(final String userName, CommentDTO comment) throws ServiceException {
        comment.setUser((User) userDetailsService.loadUserByUsername(userName));
        Comment cm = CommentDTO.getMapDtoToComment(comment);
        Link suitableLink = findSuitableLink(comment.getLSig());
        suitableLink.setCommentCount(suitableLink.getCommentCount() + 1);
        cm.setLink(suitableLink);
        comment = CommentDTO.getCommentToCommentDto(commentRepository.save(cm));
        LOGGER.info("{} SAVED COMMENT FOR LINK {}", comment, comment.getLSig());
        return comment;
    }

    private Link findSuitableLink(String linkSignature) throws ServiceException {
        return this.findLinkModelWithUser(linkSignature);
    }

    public List<Comment> findCommentsForLink(Long linkId) {
        LOGGER.info("THREAD NAME: {}", Thread.currentThread().getName());
        return commentRepository.findAllCommentsWithLinkId(linkId);
    }

    public Set<CommentDTO> findUserComments(String username) {
        final Set<Comment> userCommentsWithLink = commentRepository.getUserComments(username);
        return userCommentsWithLink.stream()
                .map(CommentDTO::getCommentToCommentDto)
                .collect(Collectors.toSet());
    }

}
