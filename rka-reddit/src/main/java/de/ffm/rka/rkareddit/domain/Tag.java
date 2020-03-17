package de.ffm.rka.rkareddit.domain;


import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import de.ffm.rka.rkareddit.domain.audit.Auditable;
import de.ffm.rka.rkareddit.domain.validator.TagResolver;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity(name="Tag")
@Getter @Setter
@ToString(exclude = "links")
@TagResolver
public class Tag extends Auditable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long tagId;
	
	@NotNull
	private String name;
	
	public Tag() {
		this.tagId = 0l;
		this.name ="";
		this.links = new ArrayList<>();
	}
	
	public Tag(@NotNull String tag) {
		this.name = tag;
	}	
	
	@ManyToMany(mappedBy= "tags")
	private Collection<Link> links;
	
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
