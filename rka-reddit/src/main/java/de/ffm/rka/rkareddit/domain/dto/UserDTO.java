package de.ffm.rka.rkareddit.domain.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import de.ffm.rka.rkareddit.domain.validator.*;
import de.ffm.rka.rkareddit.domain.validator.Validationgroups.ValidationChangeUserGroup;
import de.ffm.rka.rkareddit.domain.validator.Validationgroups.ValidationUserChangeEmail;
import de.ffm.rka.rkareddit.domain.validator.Validationgroups.ValidationUserChangePassword;
import de.ffm.rka.rkareddit.domain.validator.Validationgroups.ValidationUserRegistration;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Optional;

@EmailNotEqualToNewEmail(groups = {ValidationUserChangeEmail.class})
@NewPasswordMatcher(groups = {ValidationUserChangePassword.class})
@PasswordMatcher(groups = {ValidationUserRegistration.class})
@Getter @Setter 
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
	
	@NotEmpty(message = "mail must be entered ")
	@Size(message = "email must be between 8 and 20 signs",min = 8, max = 20)
	private String email;
	
	@NotEmpty(message = "mail must be entered ")
	@Size(message = "email must be between 8 and 20 signs",min = 8, max = 20)
	private String newEmail;
	
	@Size(message = "password must be between  5 and 20 signs",min = 5,
			max = 20, groups = {ValidationUserRegistration.class, ValidationUserChangePassword.class})
	@CorrectPassword(groups = {ValidationUserChangePassword.class, ValidationUserChangeEmail.class})
	private String password;
	
	@NotEmpty(message = "please confirm your password", groups = {ValidationUserRegistration.class})
	private String confirmPassword;
	
	@OldPasswordNewPasswordNotMatcher(groups = {ValidationUserChangePassword.class})
	@Size(message = "password must be between  5 and 20 signs",min = 5, max = 20, groups = {ValidationUserChangePassword.class})
	private String newPassword;
	
	@Size(message = "password must be between  5 and 20 signs",min = 5, max = 20, groups = {ValidationUserChangePassword.class})
	private String confirmNewPassword;
	
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
