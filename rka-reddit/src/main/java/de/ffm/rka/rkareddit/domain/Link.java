package de.ffm.rka.rkareddit.domain;


import static java.util.Date.from;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.URL;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;

import de.ffm.rka.rkareddit.domain.audit.Auditable;
import de.ffm.rka.rkareddit.util.BeanUtil;

@NamedEntityGraph(name="linkEntityGraph", attributeNodes={
	    @NamedAttributeNode("vote"),
	    @NamedAttributeNode("comments")
	})
@Entity
public class Link extends Auditable{

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long linkId;
	
	
	@NotEmpty(message = "title is required")
	private String title;
	
	@NotEmpty(message = "url is required")
	@URL(message = "valid url is required")
	private String url;
	
	@OneToMany(mappedBy = "link", fetch=FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	private List<Vote> vote = new ArrayList<>();

	private int voteCount = 0;
	
	@ManyToOne
	private User user;
		
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
    	return prettyTime.format(from(super.getCreationDate().atZone(ZONE_ID).toInstant()));
		
	}
	
	public String getHoster() throws URISyntaxException {
		URI domain = new URI(url);
		return domain.getHost().toString();
	}
	
	public void addComment(Comment comment) {
		comments.add(comment);
	}

	public Long getLinkId() {
		return linkId;
	}

	public void setLinkId(Long linkId) {
		this.linkId = linkId;
	}

	

	public List<Vote> getVote() {
		return vote;
	}


	public void setVote(List<Vote> vote) {
		this.vote = vote;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
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
		return "Link [linkId=" + linkId + ", title=" + title + ", url=" + url + ", vote=" + vote + ", voteCount="
				+ voteCount + "]";
	}

}
