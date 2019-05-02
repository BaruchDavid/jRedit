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
public class Comment {
	
	@Id
	@GeneratedValue
	private long commentId;
	private long linkId;
	private long createBy;
	private String comment;
	private LocalDateTime createdOn;
}
