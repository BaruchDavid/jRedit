package de.ffm.rka.rkareddit.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.ffm.rka.rkareddit.domain.audit.Auditable;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;


@Entity(name="User")
@Table(name = "user")
@Getter @Setter
@ToString(exclude = {"userLinks", "userComment", "roles", "profileFoto", "userClickedLinks"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends Auditable implements UserDetails, Serializable {

	private static final long serialVersionUID = -5987601453095162765L;



//	GUID globale id selbst generieren
//	UUID

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(unique = true, nullable=false)
	private String email;

	@Column(length = 100)
	private String password;

	@Lob
	@Basic(fetch=FetchType.LAZY)
	private byte[] profileFoto;

	@Column
	private LocalDateTime fotoCreationDate;

	@Column
	private LocalDateTime activationDeadLineDate;

	@Column(length = 50)
	private String firstName;


	@Column(length = 50)
	private String secondName;

	@Column(nullable = false, unique = true)
	private  String aliasName;

	@Column(nullable = true, unique = false)
	private  String newEmail;

	/**
	 * mappedBy stellt die bidirektionale Verbindung
	 * bei der Beziehung 'ein User hat viele Links'
	 * und kennzeichnet, dass 'Link' der Owner
	 * von der Beziehung ist.
	 * Und der Wert 'user' ist die Referenz auf der
	 * anderen Seite der Entität 'Link'
	 * Man muss angeben, auf welches Feld in der Entität
	 * gemappt werden soll
	 * https://javabydeveloper.com/difference-between-joincolumn-and-mappedby/
	 */
	@Builder.Default
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private Set<Link> userLinks = new HashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private Set<Comment> userComment = new HashSet<>();


	@Builder.Default
	@Column(nullable = false)
	private boolean enabled=false;


	@NotEmpty(message = "please confirm your password")
	private String confirmPassword;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "users_roles",
			joinColumns = @JoinColumn(name = "userId", referencedColumnName = "userId"),
			inverseJoinColumns = @JoinColumn(name = "roleId", referencedColumnName = "roleId")
	)
	private Set<Role> roles = new HashSet<>();

	@Builder.Default
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "usersLinksHistory")
	@JsonIgnore
	private Set<Link> userClickedLinks = new HashSet<>();

	public void addLink(Link link) {
		userLinks.add(link);
		link.setUser(this);
	}

	public void removeLink(Link link) {
		userLinks.remove(link);
		link.setUser(null);
	}

	private String activationCode;

	public User(
			String mail, String password) {
		this.email = mail;
		this.password = password;
	}

	public void addLinkToUser(Link link) {
		this.userLinks.add(link);
	}

	public void addRoles(Set<Role> roles) {
		this.roles.addAll(roles);
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		roles.forEach(role ->authorities.add(new SimpleGrantedAuthority(role.getRoleName())));
		return authorities;
	}


	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {

		return true;
	}

	@Override
	public boolean isAccountNonLocked() {

		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {

		return true;
	}

	public void addRole(Role roleUser) {
		this.roles.add(roleUser);

	}

	@Override
	public boolean equals(Object o) {
		boolean result;
		if (this == o) {
			result = true;
		} else if (!(o instanceof User)) {
			result = false;
		} else {
			User other = (User) o;
			result = Objects.equals(email, other.email);
		}
		return result;

	}

    @Override
    public int hashCode() {
    	return Objects.hash(email);
    }

    public void removeRole(Role role) {
    	this.roles.remove(role);
    	role.getUsers().remove(this);
    }
}
