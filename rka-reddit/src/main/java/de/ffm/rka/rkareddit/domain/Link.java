package de.ffm.rka.rkareddit.domain;


import static java.util.Date.from;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.URL;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;

import de.ffm.rka.rkareddit.domain.audit.Auditable;
import de.ffm.rka.rkareddit.util.BeanUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
@ToString(exclude = {"user", "comments", "vote", "tags"}) 
@NoArgsConstructor
public class Link extends Auditable{

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
	@OneToMany(mappedBy = "link", 
			 fetch=FetchType.LAZY,
			 orphanRemoval = true,
			 cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private List<Vote> vote = new ArrayList<>();
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE})
	@JoinTable(
			name = "link_tags",
			joinColumns = @JoinColumn(name = "linkId", referencedColumnName = "linkId"),
			inverseJoinColumns = @JoinColumn(name = "tagId", referencedColumnName = "tagId")
	)
	private List<Tag> tags = new ArrayList<>();

	private int voteCount = 0;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private User user;
		
	/**
	 * Comment is a owner cause of mappedBy argument
	 */
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
		return domain.getHost().toString();
	}
	
	public void addComment(Comment comment) {
		comments.add(comment);
	}
	
	public void addTag(Tag tag) {
		tags.add(tag);
	}
	
	public void removeTag(Tag tag) {
		tags.remove(tag);
	}
	
	public List<Tag> getTags(){
		return this.tags;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Link))
            return false;
        Link other = (Link) o;
 
        return linkId != null &&
        		linkId.equals(other.getLinkId());
    }
	 
    @Override
    public int hashCode() {
        return 31;
    }
}
