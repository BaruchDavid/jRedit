package de.ffm.rka.rkareddit.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;


@Entity
@Getter @Setter
@NoArgsConstructor
public class Role implements Serializable {

	private static final long serialVersionUID = -2090774371747207972L;

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
