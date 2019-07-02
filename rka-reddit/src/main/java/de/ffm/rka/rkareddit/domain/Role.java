package de.ffm.rka.rkareddit.domain;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
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
}
