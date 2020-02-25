package de.ffm.rka.rkareddit.domain;


import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import de.ffm.rka.rkareddit.domain.audit.Auditable;
import de.ffm.rka.rkareddit.domain.validator.TagResolver;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity(name="Tag")
@Data
@NoArgsConstructor
@ToString(exclude = "links")
@TagResolver
public class Tag extends Auditable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long tagId;
	
	@NotNull
	private String name;
	
	public Tag(@NotNull String tag) {
		this.name = tag;
	}	
	
	@ManyToMany(mappedBy= "tags")
	private Collection<Link> links;
}
