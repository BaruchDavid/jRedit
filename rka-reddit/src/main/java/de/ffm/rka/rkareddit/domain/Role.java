package de.ffm.rka.rkareddit.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
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
public class Role implements Serializable {

	private static final long serialVersionUID = -2090774371747207972L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long roleId;
	
	@NotNull(message = "Rollenname darf nicht null sein")
	private String name;
	
	@ManyToMany(mappedBy= "roles", fetch = FetchType.LAZY)
	private Set<User> users = new HashSet<User>();

	public Role(@NotNull(message = "Rollenname darf nicht null sein") String name) {
		super();
		this.name = name;
	}
	
	@Override
    public boolean equals(Object o) {
		boolean result;
		if (this == o) {
			result = true;
		} else if (!(o instanceof Role)) {
			result = false;
		} else {
			Role other = (Role) o;
			result = roleId != null &&
					roleId.equals(other.getRoleId());
		}

		return result;
	}
	 
    @Override
    public int hashCode() {
        return Objects.hash(roleId);
    }
}
