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
@ToString(exclude = {"user", "comments", "vote"}) 
@NoArgsConstructor
public class Link extends Auditable{

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Getter @Setter
	private Long linkId;
	
	@Getter @Setter
	@NotEmpty(message = "title is required")
	private String title;
	
	@NotEmpty(message = "url is required")
	@URL(message = "valid url is required")
	@Getter @Setter
	private String url;
	
	@OneToMany(mappedBy = "link", fetch=FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	@Getter @Setter
	private List<Vote> vote = new ArrayList<>();

	@Getter @Setter
	private int voteCount = 0;
	
	@ManyToOne
	@Getter @Setter
	private User user;
		
	@OneToMany(mappedBy="link")
	@Getter @Setter
	private List<Comment> comments = new ArrayList<>();

	@Autowired
	@Getter @Setter
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
}
