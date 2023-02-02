package de.ffm.rka.rkareddit.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.validator.link.LinkValidationGroup;
import de.ffm.rka.rkareddit.domain.validator.link.LinkValidationGroup.SizeMarker;
import de.ffm.rka.rkareddit.domain.validator.link.UniqueUrl;
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
import org.springframework.util.StringUtils;

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

    @NotEmpty(message = "title is required", groups = {SizeMarker.class})
    @Size(min = 5, max = 50, message = "title must be between 5 and 50 letters", groups = {SizeMarker.class})
    private String title;


    @Size(max = 80, message = "subtitle must be between 5 and 80 letters", groups = {SizeMarker.class})
    private String subtitle;

    @Size(max = 100, message = "maximal 100 letter allowed", groups = {SizeMarker.class})
    private String description;

    @URL(message = "valid url is required")
    @UniqueUrl(groups = {LinkValidationGroup.UniqueUrlMarker.class})
    @Size(max = 2000, message = "maximal 2000 letters in the url are allowed", groups = {SizeMarker.class})
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
    private List<TagDTO> tags = new ArrayList<>();

    /**
     * don't call childElementUser for de/serialize json
     */
    @JsonIgnore
    private User user;

    private static final int TIME_LATTERS = 13;


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
                        + this));
    }

    public String getElapsedTime() {
        prettyTime = BeanUtil.getBeanFromContext(PrettyTime.class);
        return prettyTime.format(from(creationDate.atZone(ZONE_ID).toInstant()));
    }

    public String getHoster() throws URISyntaxException {
        URI domain = new URI(unNullfyUrl(url));
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
        nullfyUrl(linkDto);
        Link link = modelMapper.map(linkDto, Link.class);
        link.setTags(linkDto.getTags()
                .stream()
                .map(TagDTO::mapTagDTOtoTag)
                .collect(Collectors.toSet()));
        return link;
    }

    private static void nullfyUrl(LinkDTO link) {
        if (!StringUtils.hasText(link.getUrl())) {
            link.setUrl(null);
        }
    }

    private static String unNullfyUrl(String url) {
        return Optional.ofNullable(url).orElse("");
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
                .map(CommentDTO::mapCommentToCommentDto)
                .collect(Collectors.toSet()));

        linkDto.setTags(link.getTags()
                .stream()
                .map(TagDTO::mapTagToTagDTO)
                .collect(Collectors.toList()));
        linkDto.setLinkSignature(createLinkSignature(link));
        return linkDto;
    }

    public static LinkDTO mapLinkToDto(Link link) {
        final LinkDTO linkDTO = modelMapper.map(link, LinkDTO.class);
        linkDTO.setUrl(unNullfyUrl(linkDTO.getUrl()));
        linkDTO.setLinkSignature(LinkDTO.createLinkSignature(link));
        return linkDTO;
    }


    /**
     * @param timeInSeconds which represent creation date
     * @return creation date as localdatetime
     */
    public static long convertEpochSecToLinkId(final String timeInSeconds) throws ServiceException {
        long id;
        try {
            id = Long.parseLong(timeInSeconds.substring(TIME_LATTERS));
        } catch (IndexOutOfBoundsException | NumberFormatException ex) {
            throw new ServiceException("NO VALID LINK-SIGNATURE: " + timeInSeconds);
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
