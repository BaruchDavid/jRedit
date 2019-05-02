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
public class Link {

	@Id
	@GeneratedValue
	private long linkId;
	private long createdBy;
	private String title;
	private String url;
	private LocalDateTime createOn;
}
