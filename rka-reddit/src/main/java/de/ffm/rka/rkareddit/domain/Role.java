package de.ffm.rka.rkareddit.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;


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
	private Collection<User> users;

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
