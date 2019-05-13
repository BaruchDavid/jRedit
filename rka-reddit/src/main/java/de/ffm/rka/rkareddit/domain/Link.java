package de.ffm.rka.rkareddit.domain;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;

import de.ffm.rka.rkareddit.model.audit.Auditable;
import de.ffm.rka.rkareddit.util.BeanUtil;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Date.from;

import java.net.URI;
import java.net.URISyntaxException;

@Entity
@NoArgsConstructor
@Getter 
@Setter
public class Link extends Auditable{

	@Id
	@GeneratedValue
	private Long linkId;
	private long userId;
	private String title;
	private String url;
	private LocalDateTime createdOn;
	private int voteCount = 0;
	
	
	@OneToMany(mappedBy="link")
	private List<Comment> comments = new ArrayList<>();

	@Autowired
	private transient PrettyTime prettyTime;
	
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

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
	
}
