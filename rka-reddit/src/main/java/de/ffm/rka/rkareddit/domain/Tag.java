package de.ffm.rka.rkareddit.domain;


import de.ffm.rka.rkareddit.domain.audit.Auditable;
import de.ffm.rka.rkareddit.domain.validator.TagResolver;
import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

@Entity(name="Tag")
@Getter @Setter
@ToString(exclude = "links")
@TagResolver
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag extends Auditable implements Serializable {
	
	private static final long serialVersionUID = -1764376324929313404L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long tagId;
	
	@NotNull
	@Column(name = "tagName") 
	private String tagName;
		
	@Builder.Default
	@ManyToMany(fetch = FetchType.LAZY, mappedBy= "tags")
	private Set<Link> links = new HashSet<>();
	
	public void addLink(Link link) {
		links.add(link);
	}
	
	public void removeLink(Link link) {
		links.remove(link);
	}
	
	@Override
    public boolean equals(Object o) {
		boolean result;
		if (this == o) {
			result = true;
		} else if (!(o instanceof Tag)) {
			result = false;
		} else {
			Tag other = (Tag) o;
			result = tagId != null &&
					tagId.equals(other.getTagId());
		}

		return result;
	}
	 
    @Override
    public int hashCode() {
        return Objects.hash(tagId);
    }
}
