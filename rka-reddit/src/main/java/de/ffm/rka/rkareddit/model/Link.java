package de.ffm.rka.rkareddit.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import de.ffm.rka.rkareddit.model.audit.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
public class Link extends Auditable{

	@Id
	@GeneratedValue
	private long linkId;
	private long createdBy;
	private String title;
	private String url;
	private LocalDateTime createOn;
	
	@OneToMany(mappedBy="link")
	private List<Comment> comments = new ArrayList<>();
}
