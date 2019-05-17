package de.ffm.rka.rkareddit.domain;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.URL;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;

import de.ffm.rka.rkareddit.domain.audit.Auditable;
import de.ffm.rka.rkareddit.util.BeanUtil;

import static java.util.Date.from;

import java.net.URI;
import java.net.URISyntaxException;

@Entity
public class Link extends Auditable{

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long linkId;
	private long userId;
	
	@NotEmpty(message = "title is required")
	private String title;
	
	@NotEmpty(message = "url is required")
	@URL(message = "valid url is required")
	private String url;
	
	@OneToMany(mappedBy = "link")
	private List<Vote> vote = new ArrayList<>();
	
	private LocalDateTime createdOn;
	private int voteCount = 0;
		
	
	@OneToMany(mappedBy="link")
	private List<Comment> comments = new ArrayList<>();

	@Autowired
	private transient PrettyTime prettyTime;
	
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    public Link() {
	}

    
	public Link(String title, String url) {

		this.title = title;
		this.url = url;
	}
	
	public String getElapsedTime() {
		prettyTime = BeanUtil.getBeanFromContext(PrettyTime.class);
		return prettyTime.format(from(this.createdOn.atZone(ZONE_ID).toInstant()));
	}
	
	public void addComment(Comment comment) {
		comments.add(comment);
	}
	
	
	public String getDomainName() throws URISyntaxException {
        URI uri = new URI(this.url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

	
	public Long getLinkId() {
		return linkId;
	}

	public void setLinkId(Long linkId) {
		this.linkId = linkId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	public int getVoteCount() {
		return voteCount;
	}

	public void setVoteCount(int voteCount) {
		this.voteCount = voteCount;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public PrettyTime getPrettyTime() {
		return prettyTime;
	}

	public void setPrettyTime(PrettyTime prettyTime) {
		this.prettyTime = prettyTime;
	}

	public static ZoneId getZoneId() {
		return ZONE_ID;
	}

	@Override
	public String toString() {
		return "Link [linkId=" + linkId + ", userId=" + userId + ", title=" + title + ", url=" + url + ", createdOn="
				+ createdOn + ", voteCount=" + voteCount + ", comments=" + comments + ", prettyTime=" + prettyTime
				+ "]";
	}
	
}
