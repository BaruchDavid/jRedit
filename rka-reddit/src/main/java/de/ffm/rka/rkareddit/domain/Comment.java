package de.ffm.rka.rkareddit.domain;

import static java.util.Date.from;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;

import de.ffm.rka.rkareddit.domain.audit.Auditable;
import de.ffm.rka.rkareddit.util.BeanUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
public class Comment extends Auditable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long commentId;
	private String commentText;
	
	@ManyToOne(fetch = FetchType.LAZY)
	//@NotNull
	private User user;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Link link;

	@Autowired
	private transient PrettyTime prettyTime;
	
	private transient LocalDateTime createdOn;
	
	private static final ZoneId ZONE_ID = ZoneId.systemDefault();
	
	public String getElapsedTime() {
		prettyTime = BeanUtil.getBeanFromContext(PrettyTime.class);
		return prettyTime.format(from(super.getCreationDate().atZone(ZONE_ID).toInstant()));
	}
	
	
	public long getCommentId() {
		return commentId;
	}

	public void setCommentId(long commentId) {
		this.commentId = commentId;
	}

	

	public String getCommentText() {
		return commentText;
	}


	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}


	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}
}
