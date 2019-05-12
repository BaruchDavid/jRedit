package de.ffm.rka.rkareddit.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
public class Vote {
	
	@Id
	@GeneratedValue
	private long voteId;
	private long userId;
	private long createdBy;
	private  LocalDateTime commentOn;
	
	
}
