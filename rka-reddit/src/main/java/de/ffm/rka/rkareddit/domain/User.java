package de.ffm.rka.rkareddit.domain;

import de.ffm.rka.rkareddit.domain.audit.Auditable;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


@Entity

@Getter @Setter 
@ToString(exclude = {"userLinks", "userComments", "roles", "profileFoto", "userClickedLinks"})
@AllArgsConstructor
@Builder
public class User extends Auditable implements UserDetails, Serializable {

	public User() {
		this.enabled=false;
	}
	
	private static final long serialVersionUID = -5987601453095162765L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(unique = true, nullable=false)
	private String email;
	
	@Column(length = 100)
	private String password;
	
	@Lob
	private byte[] profileFoto;

	@Column(length = 50)
	private String firstName;
	
	@Column(length = 50)
	private String secondName;
	
	@Column(nullable = false, unique = true)
	private  String aliasName;
	
	@Column(nullable = true, unique = false)
	private  String newEmail;

	/**
	 * Link is a owner of this Relation
	 */
	@Builder.Default
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<Link> userLinks = new ArrayList<>();
	
	/**
	 * Comment is a owner of this Relation
	 */
	@Builder.Default
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<Comment> userComments = new ArrayList<>();
	
	@Builder.Default
	@Column(nullable = false)
	private boolean enabled=false;

	
	@NotEmpty(message = "please confirm your password")
	private String confirmPassword;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(
			name = "users_roles",
			joinColumns = @JoinColumn(name = "userId", referencedColumnName = "userId"),
			inverseJoinColumns = @JoinColumn(name = "roleId", referencedColumnName = "roleId")
	)
	private Set<Role> roles = new HashSet<>();
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(
			name = "user_clickedLinks",
			joinColumns = @JoinColumn(name = "userId", referencedColumnName = "userId"),
			inverseJoinColumns = @JoinColumn(name = "linkId", referencedColumnName = "linkId")
	)
	private List<Link> userClickedLinks = new ArrayList<>();
	

	private String activationCode;

	public User(
			String mail, String password) {
		this.email = mail;
		this.password = password;
	}

	public void addCommentToUser(Comment comment) {
		this.userComments.add(comment);
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
		roles.stream()
			.map(role -> authorities.add(new SimpleGrantedAuthority(role.getName())))
			.collect(Collectors.toList());
		
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
			result = userId != null && userId.equals(other.getUserId());
		}
		return result;
	}
	 
    @Override
    public int hashCode() {
        return 31;
    }
	
}
