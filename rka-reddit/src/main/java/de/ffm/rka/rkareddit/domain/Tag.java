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
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity(name="Tag")
@Data
@NoArgsConstructor
@ToString
public class Tag extends Auditable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long tagId;
	
	@NotNull
	private String name;
	
	public Tag(@NotNull String tag) {
		this.name = tag;
	}	
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(
			name = "link_tags",
			joinColumns = @JoinColumn(name = "tagId", referencedColumnName = "tagId"),
			inverseJoinColumns = @JoinColumn(name = "linkId", referencedColumnName = "linkId")
	)
	private Collection<Link> links;
}
