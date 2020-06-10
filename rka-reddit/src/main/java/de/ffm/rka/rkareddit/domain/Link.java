package de.ffm.rka.rkareddit.domain;


import de.ffm.rka.rkareddit.domain.audit.Auditable;
import de.ffm.rka.rkareddit.util.BeanUtil;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.URL;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Date.from;

@Entity
@Getter @Setter
@ToString(exclude = {"user", "comments", "vote", "tags", "users"}) 
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Link extends Auditable implements Serializable{

	private static final long serialVersionUID = -5337989744648444109L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long linkId;
	
	@NotEmpty(message = "title is required")
	private String title;
	
	@NotEmpty(message = "url is required")
	@URL(message = "valid url is required")
	private String url;
	
	/**
	 * Vote is owner of this Relation
	 */
	@Builder.Default
	@OneToMany(mappedBy = "link", 
			 fetch=FetchType.LAZY,
			 orphanRemoval = true,
			 cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JsonIgnore
	private List<Vote> vote = new ArrayList<>();
	
	@Builder.Default
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST})
	@JoinTable(
			name = "link_tags",
			joinColumns = @JoinColumn(name = "linkId", referencedColumnName = "linkId"),
			inverseJoinColumns = @JoinColumn(name = "tagId", referencedColumnName = "tagId")
	)
	@JsonIgnore
	private List<Tag> tags = new ArrayList<>();

	@ManyToMany(mappedBy= "userClickedLinks")
	@JsonIgnore
	private List<User> usersLinksHistory;
	
	@Builder.Default
	private int voteCount = 0;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JsonIgnore
	private User user;
		
	/**
	 * Comment is a owner cause of mappedBy argument
	 */
	@Builder.Default
	@OneToMany(mappedBy="link", fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	private List<Comment> comments = new ArrayList<>();

	@Autowired
	private transient PrettyTime prettyTime;
	
	@Getter 
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

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
		return domain.getHost();
	}
	
	public void addVote(Vote vote) {
		this.vote.add(vote);
		vote.setLink(this);
	}
	
	public void removeVote(Vote vote) {
		this.vote.remove(vote);
		vote.setLink(null);
	}
	
	public void addComment(Comment comment) {
		comments.add(comment);
		comment.setLink(this);
	}
	
	public void remoteComment(Comment comment) {
		this.comments.add(comment);
		comment.setLink(null);
	}
	
	public void addTag(Tag tag) {
		tags.add(tag);
		tag.getLinks().add(this);
	}
	
	public void removeTag(Tag tag) {
		tags.remove(tag);
		tag.getLinks().remove(this); 
	}
	
	public List<Tag> getTags(){
		return this.tags;
	}

	@Override
    public boolean equals(Object o) {
		boolean result;
		if (this == o) {
			result = true;
		} else if (!(o instanceof Link)) {
			result = false;
		} else {
			Link other = (Link) o;
			result = linkId != null &&
					linkId.equals(other.getLinkId());
		}
		return result;
	}
	 
    @Override
    public int hashCode() {
        return 31;
    }
}
