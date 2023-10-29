package de.ffm.rka.rkareddit.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Entity
@ToString(exclude = "users")
@Getter @Setter
@NoArgsConstructor
public class Role implements Serializable {

	private static final long serialVersionUID = -2090774371747207972L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long roleId;
	
	@NotNull(message = "Rollenname darf nicht null sein")
	@Column(name = "name")
	private String roleName;
	
	@ManyToMany(mappedBy= "roles", fetch = FetchType.LAZY)
	private Set<User> users = new HashSet<>();

	public Role(@NotNull(message = "Rollenname darf nicht null sein") String name) {
		super();
		this.roleName = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Role)) {
			return false;
		}
		Role role = (Role) o;
		return roleName.equals(role.roleName);
	}

	@Override
    public int hashCode() {
        return Objects.hash(roleName);
    }
}
