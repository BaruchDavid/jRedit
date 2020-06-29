package de.ffm.rka.rkareddit.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.ffm.rka.rkareddit.domain.*;
import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.util.BeanUtil;
import lombok.*;
import org.hibernate.validator.constraints.URL;
import org.modelmapper.ModelMapper;
import org.ocpsoft.prettytime.PrettyTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static java.util.Date.from;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkDTO implements Serializable {

	private static final ModelMapper modelMapper = new ModelMapper();
	private static final long serialVersionUID = 1748419915925154370L;
	private static final Logger LOGGER = LoggerFactory.getLogger(LinkDTO.class);

	@NotEmpty(message = "title is required")
	@Size(min=5, max = 50, message = "maximal 50 letter allowed")
	private String title;

	@Size(min=0, max = 100, message = "maximal 100 letter allowed")
	@Column(length = 100, nullable = true)
	private String description;

	@NotEmpty(message = "url is required")
	@URL(message = "valid url is required")
	private String url;

	@Autowired
	private transient PrettyTime prettyTime;

	@Getter
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();
	private int voteCount = 0;
	private String createdBy;
	private String lastModifiedBy;
	private LocalDateTime creationDate;
	private LocalDateTime lastModifiedDate;
	private List<CommentDTO> comments = new ArrayList<>();
	private List<Vote> vote = new ArrayList<>();
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

	public String getLinkSignature(){
		return this.linkSignature;
	}

	public LinkDTO(String title, String url) {
		this.title = title;
		this.url = url;
	}
	
	public String getElapsedTime() {
		prettyTime = BeanUtil.getBeanFromContext(PrettyTime.class);
    	return prettyTime.format(from(creationDate.atZone(ZONE_ID).toInstant()));
	}
	
	public String getHoster() throws URISyntaxException {
		URI domain = new URI(url);
		return domain.getHost();
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

	private List<Tag> tags = new ArrayList<>();

	/**
	 *
	 * @param time creation date
	 * @return creatation date as milli
	 */
	public static String convertLDTtoEpochSec(LocalDateTime time){
		Instant instant = time.atZone(ZoneId.systemDefault()).toInstant();
		return String.valueOf(instant.toEpochMilli());
	}

	public static Link getMapDtoToLink(LinkDTO linkDto){
		return modelMapper.map(linkDto, Link.class);
	}

	public static LinkDTO getMapLinkToDto(Link link){
		LinkDTO temp = modelMapper.map(link, LinkDTO.class);
		temp.setLinkSignature(convertLDTtoEpochSec(link.getCreationDate())
							.concat(String.valueOf(link.getLinkId())));
		return temp;
	}

	/**
	 *
	 * @param timeInSeconds which represend creation date
	 * @return creation date as localdatetime
	 */
	public static long convertEpochSecToId(final String timeInSeconds) throws IllegalArgumentException {
		long id = 0l;
		try {
			id = Long.valueOf(timeInSeconds.substring(TIME_LATTERS, timeInSeconds.length()));
		} catch(Exception ex){
			String msg = "NO VALID LINK-SIGNATURE: ".concat(timeInSeconds);
			LOGGER.error(msg.concat(timeInSeconds), ex);
			throw new IllegalArgumentException(msg.concat(timeInSeconds));
		}
		return id;
	}

	@Override
    public int hashCode() {
        return 31;
    }

}