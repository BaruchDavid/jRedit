package de.ffm.rka.rkareddit.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import de.ffm.rka.rkareddit.model.audit.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
public class Comment extends Auditable{
	
	@Id
	@GeneratedValue
	private long commentId;
	
	@Column
	private long linkId;
	
	@Column
	private long createBy;
	
	@Column
	private String comment;
	
	@Column
	private LocalDateTime createdOn;
	
	@ManyToOne
	private Link link;
	
	
}
