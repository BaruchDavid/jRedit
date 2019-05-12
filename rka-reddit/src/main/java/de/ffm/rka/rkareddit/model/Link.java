package de.ffm.rka.rkareddit.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
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
	
	@Column
	private long createdBy;
	
	@Column
	private String title;
	
	@Column
	private String url;
	
	@Column
	private LocalDateTime createdOn;
	
	@OneToMany(mappedBy="link")
	private List<Comment> comments = new ArrayList<>();

	public Link(String title, String url) {

		this.title = title;
		this.url = url;
	}
	
	
	public void addComment(Comment comment) {
		comments.add(comment);
	}
	
}
