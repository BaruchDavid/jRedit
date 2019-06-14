package de.ffm.rka.rkareddit.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import de.ffm.rka.rkareddit.domain.audit.Auditable;
import de.ffm.rka.rkareddit.domain.validator.PasswordMatcher;


@Entity
@PasswordMatcher
public class User extends Auditable implements UserDetails, Serializable {


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
	

	@NotEmpty(message = "you must enter First Name.")
	@Column(length = 50)
	private String firstName;
	
	@NotEmpty(message = "you must enter Second Name.")
	@Column(length = 50)
	private String secondName;
	
	@Transient
	private  String fullName;
	
	@NotEmpty(message = "Please enter alias.")
	@Column(nullable = false, unique = true)
	private  String aliasName;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private Set<Link> userLinks = new HashSet<Link>();
	
	@OneToMany(mappedBy = "user")
	private Set<Comment> userComments = new HashSet<Comment>();
	
	@NotNull
	@Column(nullable = false)
	private boolean enabled;

	@Transient
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
	
	public void addCommentToUser(Comment comment) {
		this.userComments.add(comment);
	}
	
	
	
	public Set<Comment> getUserComments() {
		return userComments;
	}



	public void setUserComments(Set<Comment> userComments) {
		this.userComments = userComments;
	}



	public void addLinkToUser(Link link) {
		this.userLinks.add(link);
	}
	
	public Set<Link> getUserLinks() {
		return userLinks;
	}

	public void setUserLinks(Set<Link> userLinks) {
		this.userLinks = userLinks;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
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

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getFullName() {
		
		return firstName.concat(" ").concat(secondName);
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void addRole(Role role_user) {
		this.roles.add(role_user);
		
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", email=" + email + ", password=" + password + ", firstName=" + firstName
				+ ", secondName=" + secondName + ", fullName=" + fullName + ", aliasName=" + aliasName + ", enabled="
				+ enabled + ", confirmPassword=" + confirmPassword + ", activationCode=" + activationCode + "]";
	}





	
	
}
