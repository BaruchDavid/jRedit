package de.ffm.rka.rkareddit.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import de.ffm.rka.rkareddit.domain.audit.Auditable;
import de.ffm.rka.rkareddit.domain.validator.PasswordMatcher;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
//@PasswordMatcher
@Getter @Setter 
@ToString(exclude = {"userLinks", "userComments", "roles", "profileFoto"})
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
	
	@NotEmpty(message = "mail must be entered ")
	@Size(message = "email must be between 8 and 20 signs",min = 8, max = 20)
	@Column(unique = true, nullable=false)
	private String email;
	
	@Column(length = 100)
	private String password;
	
	@Lob
	private byte[] profileFoto;

	@NotEmpty(message = "you must enter First Name.")
	@Column(length = 50)
	private String firstName;
	
	@NotEmpty(message = "you must enter Second Name.")
	@Column(length = 50)
	private String secondName;
	
	@NotEmpty(message = "Please enter alias.")
	@Column(nullable = false, unique = true)
	private  String aliasName;

	/**
	 * Link is a owner of this Relation
	 */
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<Link> userLinks = new ArrayList<Link>();
	
	/**
	 * Comment is a owner of this Relation
	 */
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<Comment> userComments = new ArrayList<Comment>();
	
	@NotNull
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

	private String activationCode;

	public User(
			@NotEmpty(message = "mail must be entered ") 
			@Size(message = "email must be between 8 and 20 signs", min = 8, max = 20) String email,
			String password,
			Set<Role> roles) {
		super();
		this.email = email;
		this.password = password;
	}

	public void addCommentToUser(Comment comment) {
		this.userComments.add(comment);
	}

	public void addLinkToUser(Link link) {
		this.userLinks.add(link);
	}

	public void add(Role role) {
		roles.add(role);
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
	
	public void addRole(Role role_user) {
		this.roles.add(role_user);
		
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof User))
			return false;
		User other = (User) o;
		return userId != null && userId.equals(other.getUserId());
	}
	 
    @Override
    public int hashCode() {
        return 31;
    }
	
}
