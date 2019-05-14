package de.ffm.rka.rkareddit.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
public class Vote {
	
	@Id
	@GeneratedValue
	private long voteId;
	private long userId;
	private long createdBy;
	private  LocalDateTime commentOn;
	public long getVoteId() {
		return voteId;
	}
	public void setVoteId(long voteId) {
		this.voteId = voteId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}
	public LocalDateTime getCommentOn() {
		return commentOn;
	}
	public void setCommentOn(LocalDateTime commentOn) {
		this.commentOn = commentOn;
	}
	
	
	
}
