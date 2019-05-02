package de.ffm.rka.rkareddit.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Vote {
	
	@Id
	@GeneratedValue
	private long voteId;
	private long linkId;
	private long createdBy;
	private  LocalDateTime commentOn;
	
	
}
