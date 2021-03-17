package de.ffm.rka.rkareddit.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.Tag;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.exception.IllegalVoteException;
import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.util.BeanUtil;
import lombok.*;
import org.hibernate.validator.constraints.URL;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.ocpsoft.prettytime.PrettyTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Date.from;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinkDTO implements Serializable {

    private static ModelMapper modelMapper;
    private static final long serialVersionUID = 1748419915925154370L;
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkDTO.class);

    static {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        modelMapper.addMappings(new PropertyMap<Link, LinkDTO>() {
            @Override
            protected void configure() {
                skip(destination.getCommentDTOS());
                skip(destination.getTags());
            }
        });
    }

    @NotEmpty(message = "title is required")
    @Size(min = 5, max = 50, message = "maximal 50 letter allowed")
    private String title;

    @NotEmpty(message = "title is required")
    @Size(min = 5, max = 80, message = "maximal 80 letter allowed")
    private String subtitle;

    @Size(min = 0, max = 100, message = "maximal 100 letter allowed")
    private String description;

    @NotEmpty(message = "url is required")
    @URL(message = "valid url is required")
    private String url;

    @Autowired
    private transient PrettyTime prettyTime;

    @Getter
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();
    private int voteCount = 0;
    private int commentCount = 0;
    private String createdBy;
    private String lastModifiedBy;
    private LocalDateTime creationDate;
    private LocalDateTime lastModifiedDate;

    /**
     * don't call childElementUser for de/serialize json
     */
    @JsonIgnore
    private Set<CommentDTO> commentDTOS = new HashSet<>();


    /**
     * don't call childElementUser for de/serialize json
     */
    @JsonIgnore
    private List<Tag> tags = new ArrayList<>();

    /**
     * don't call childElementUser for de/serialize json
     */
    @JsonIgnore
    private User user;

    private static final int TIME_LATTERS = 13;

    @JsonIgnore
    private String linkSignature;

    /**
     * linkId is necessary to map
     * already saved link for updating,
     * due to need linkId for update
     */
    private Long linkId;

    public String getLinkSignature() throws ServiceException {
        return Optional.ofNullable(this.linkSignature)
                .orElseThrow(() -> new ServiceException("NO Link-Signature for this Link: "
                        + this.toString()));
    }

    public String getElapsedTime() {
        prettyTime = BeanUtil.getBeanFromContext(PrettyTime.class);
        return prettyTime.format(from(creationDate.atZone(ZONE_ID).toInstant()));
    }

    public String getHoster() throws URISyntaxException {
        URI domain = new URI(url);
        return domain.getHost();
    }

    /**
     * @param link creation date
     * @return creation date as milli
     */
    public static String createLinkSignature(Link link) {
        Instant instant = link.getCreationDate().atZone(ZoneId.systemDefault()).toInstant();
        return String.valueOf(instant.toEpochMilli()) + link.getLinkId();
    }

    public static Link getMapDtoToLink(LinkDTO linkDto) {
        return modelMapper.map(linkDto, Link.class);
    }

    /**
     * LinkController and AuthController maps links to LinkDTO.
     * AuthController needs already presented comments from link,
     * otherwise it get Lazy Initialization
     *
     * @param link to be filled with comments and for mapping
     * @return linkDto from link
     */
    public static LinkDTO mapFullyLinkToDto(Link link) {
        LinkDTO linkDto = mapLinkToDto(link);
        linkDto.setCommentDTOS(link.getComments().stream()
                .map(CommentDTO::getCommentToCommentDto)
                .collect(Collectors.toSet()));
        linkDto.setLinkSignature(createLinkSignature(link));
        return linkDto;
    }

    public static LinkDTO mapLinkToDto(Link link) {
        final LinkDTO linkDTO = modelMapper.map(link, LinkDTO.class);
        linkDTO.setLinkSignature(LinkDTO.createLinkSignature(link));
        return linkDTO;
    }


    /**
     * @param timeInSeconds which represent creation date
     * @return creation date as localdatetime
     */
    public static long convertEpochSecToId(final String timeInSeconds) {
        long id = 0L;
        try {
            id = Long.parseLong(timeInSeconds.substring(TIME_LATTERS));
        } catch (Exception ex) {
            String msg = "NO VALID LINK-SIGNATURE: ".concat(timeInSeconds);
            LOGGER.error(msg.concat(timeInSeconds), ex);
            throw new IllegalVoteException(msg.concat(timeInSeconds));
        }
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, subtitle, description, url, linkSignature);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkDTO linkDTO = (LinkDTO) o;
        return Objects.equals(title, linkDTO.title) &&
                Objects.equals(description, linkDTO.description) &&
                Objects.equals(url, linkDTO.url);
    }

    @Override
    public String toString() {
        return "LinkDTO [title=" + title + ", description=" + description + ", url=" + url + ", voteCount=" + voteCount
                + ", commentCount=" + commentCount + ", linkSignature=" + linkSignature + ", linkId=" + linkId + "]";
    }

}
