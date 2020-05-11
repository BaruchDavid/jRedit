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
	
	/** marker interface for user changes */
	public interface ValidationChangeUserGroup {
	}
	
	/** marker interface for user registration */
	public interface ValidationUserRegistration {
	}
	
	@NotEmpty(message = "mail must be entered ")
	@Size(message = "email must be between 8 and 20 signs",min = 8, max = 20)
	private String email;
	
	@Size(message = "password must be between  5 and 20 signs",min = 5, max = 20, groups = {ValidationUserRegistration.class})
	private String password;
	
	@NotEmpty(message = "please confirm your password", groups = {ValidationUserRegistration.class})
	private String confirmPassword;
	
	@NotEmpty(message = "please type new your new password")
	private String newPassword;
	
	@NotEmpty(message = "you must enter First Name.", groups = {ValidationChangeUserGroup.class})
	private String firstName;
	
	@NotEmpty(message = "you must enter Second Name.", groups = {ValidationChangeUserGroup.class})
	private String secondName;
	
	@JsonIgnore
	private  String fullName;
	
	@NotEmpty(message = "Please enter alias.", groups = {ValidationChangeUserGroup.class})
	@Size(min = 5, message = "at least 5 characters for alias name", groups = {ValidationChangeUserGroup.class})
	private  String aliasName;
	
	private String activationCode;
		
	public String getFullName() {
		String fName = Optional.ofNullable(firstName).orElse("");
		String sName = Optional.ofNullable(secondName).orElse("");
		fullName = fName.concat(" ").concat(sName);
		return fullName;
	}
}
