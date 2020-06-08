package de.ffm.rka.rkareddit.domain;


import de.ffm.rka.rkareddit.domain.audit.Auditable;
import de.ffm.rka.rkareddit.domain.validator.TagResolver;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long tagId;
	
	@NotNull
	@Column(name = "tagName") 
	private String tagName;
		
	@Builder.Default
	@ManyToMany(mappedBy= "tags")
	private List<Link> links = new ArrayList<>();
	
	public void addLink(Link link) {
		links.add(link);
	}
	
	public void removeLink(Link link) {
		links.remove(link);
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag))
            return false;
        Tag other = (Tag) o;
 
        return tagId != null &&
        		tagId.equals(other.getTagId());
    }
	 
    @Override
    public int hashCode() {
        return 31;
    }
}
