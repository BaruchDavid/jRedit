package de.ffm.rka.rkareddit.domain;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter @Setter
@NoArgsConstructor
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long roleId;
	
	@NotNull(message = "Rollenname darf nicht null sein")
	private String name;
	
	@ManyToMany(mappedBy= "roles")
	private Collection<User> users;

	public Role(@NotNull(message = "Rollenname darf nicht null sein") String name) {
		super();
		this.name = name;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role))
            return false;
        Role other = (Role) o;
 
        return roleId != null &&
        		roleId.equals(other.getRoleId());
    }
	 
    @Override
    public int hashCode() {
        return 31;
    }
}
