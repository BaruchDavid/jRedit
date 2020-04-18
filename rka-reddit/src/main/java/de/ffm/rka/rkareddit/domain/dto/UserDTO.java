package de.ffm.rka.rkareddit.domain.dto;


import java.util.Optional;


import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import de.ffm.rka.rkareddit.domain.validator.PasswordMatcher;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@PasswordMatcher
@Getter @Setter 
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

	@NotEmpty(message = "mail must be entered ")
	@Size(message = "email must be between 8 and 20 signs",min = 8, max = 20)
	@Column(unique = true, nullable=false)
	private String email;
	
	@Column(length = 100)
	private String password;
	
	@NotEmpty(message = "please confirm your password")
	private String confirmPassword;
	
	@NotEmpty(message = "you must enter First Name.")
	@Column(length = 50)
	private String firstName;
	
	@NotEmpty(message = "you must enter Second Name.")
	@Column(length = 50)
	private String secondName;
	
	@JsonIgnore
	private  String fullName;
	
	@NotEmpty(message = "Please enter alias.")
	@Column(nullable = false, unique = true)
	private  String aliasName;
	
	private String activationCode;
		
	public String getFullName() {
		String fName = Optional.ofNullable(firstName).orElse("");
		String sName = Optional.ofNullable(secondName).orElse("");
		fullName = fName.concat(" ").concat(sName);
		return fullName;
	}	
}
