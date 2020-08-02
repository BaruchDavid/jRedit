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
import org.hibernate.validator.constraints.Length;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.util.ArrayList;

import java.util.List;
import java.util.Objects;

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
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private Long linkId;
	
	@NotEmpty(message = "title is required")
	@Column(length = 50)
	private String title;

	@Column(length = 100, nullable = true)
	private String description;
	
	@Column(nullable = false, unique = true)
	private String url;
	

	@Builder.Default
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST})
	@JoinTable(
			name = "link_tags",
			joinColumns = @JoinColumn(name = "linkId", referencedColumnName = "linkId"),
			inverseJoinColumns = @JoinColumn(name = "tagId", referencedColumnName = "tagId")
	)
	@JsonIgnore
	@Fetch(FetchMode.SUBSELECT)
	private List<Tag> tags = new ArrayList<>();

	@Builder.Default
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(
			name = "user_clickedLinks",
			joinColumns = @JoinColumn(name = "linkId", referencedColumnName = "linkId"),
			inverseJoinColumns = @JoinColumn(name = "userId", referencedColumnName = "userId")
	)
	@JsonIgnore
	private List<User> usersLinksHistory = new ArrayList<>();
	
	@Builder.Default
	private int voteCount = 0;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "user_id")
	private User user;
		
	/**
	 * Comment is a owner cause of mappedBy argument
	 */
	@Builder.Default
	@OneToMany(mappedBy="link", fetch = FetchType.LAZY)
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

	public void addComment(Comment comment) {
		comments.add(comment);
		comment.setLink(this);
	}
	
	public void removeComment(Comment comment) {
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
		if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Link link = (Link) o;
        return Objects.equals(url, link.url);
	}
	 
    @Override
    public int hashCode() {
    	return Objects.hash(linkId);
    }

	public void addUserToLinkHistory(User userModel) {
		this.usersLinksHistory.add(userModel);
		//userModel.getUserClickedLinks().add(this); 
	}
	
	public void removeUserFromHistory(User user) {
		this.usersLinksHistory.remove(user);
		//user.getUserClickedLinks().remove(this);
	}
}
