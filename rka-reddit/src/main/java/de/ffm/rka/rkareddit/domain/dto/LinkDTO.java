package de.ffm.rka.rkareddit.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.ffm.rka.rkareddit.domain.Tag;
import de.ffm.rka.rkareddit.domain.audit.Auditable;
import de.ffm.rka.rkareddit.util.BeanUtil;
import lombok.*;
import org.hibernate.validator.constraints.URL;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static java.util.Date.from;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinkDTO {


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

	private String createdBy;
	private String lastModifiedBy;
	private LocalDateTime creationDate;
	private LocalDateTime lastModifiedDate;

	@JsonIgnore
	private String linkSignature;

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

	@Override
    public int hashCode() {
        return 31;
    }

}
