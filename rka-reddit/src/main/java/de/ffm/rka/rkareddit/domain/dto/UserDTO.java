package de.ffm.rka.rkareddit.domain.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;

import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.validator.user.*;
import de.ffm.rka.rkareddit.domain.validator.user.UserValidationgroups.ValidationChangeUserProperties;
import de.ffm.rka.rkareddit.domain.validator.user.UserValidationgroups.ValidationUserChangeEmail;
import de.ffm.rka.rkareddit.domain.validator.user.UserValidationgroups.ValidationUserChangePassword;
import de.ffm.rka.rkareddit.domain.validator.user.UserValidationgroups.ValidationUserRegistration;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EmailNotEqualToNewEmail(groups = {ValidationUserChangeEmail.class})
@NewPasswordMatcher(groups = {ValidationUserChangePassword.class})
@PasswordMatcher(groups = {ValidationUserRegistration.class, ValidationUserChangeEmail.class})
@Getter @Setter 
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
	
	private static ModelMapper modelMapper;
	
	static {
		modelMapper	= new ModelMapper();
		modelMapper.getConfiguration().setAmbiguityIgnored(true);
		modelMapper.addMappings(new PropertyMap<User, UserDTO>() {
		    @Override
		    protected void configure() {
		        skip(destination.getUserComment());
		        skip(destination.getUserLinks());
		        skip(destination.getLastModifiedDate());
		        
		    }
		});
	}
	
	@NotEmpty(message = "mail must be entered ", groups = {ValidationUserRegistration.class})
	@Size(message = "email must be between 8 and 20 signs",min = 8, max = 20,  groups = {ValidationUserRegistration.class})
	private String email;
	
	@NotEmpty(message = "mail must be entered ", groups = {ValidationUserChangeEmail.class})
	@Size(message = "email must be between 8 and 20 signs",min = 8, max = 20, groups = {ValidationUserChangeEmail.class})
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
	
	@NotEmpty(message = "you must enter First Name.", groups = {ValidationChangeUserProperties.class,
																ValidationUserRegistration.class})
	private String firstName;
	
	@NotEmpty(message = "you must enter Second Name.", groups = {ValidationChangeUserProperties.class,
																ValidationUserRegistration.class})
	private String secondName;
	
	@JsonIgnore
	private  String fullName;
	
	@NotEmpty(message = "Please enter alias.", groups = {ValidationChangeUserProperties.class,
															ValidationUserRegistration.class})
	@Size(min = 5, message = "at least 5 characters for alias name", groups = {ValidationChangeUserProperties.class,
																				ValidationUserRegistration.class})
	private  String aliasName;
	
	private String activationCode;
		
	private List<Comment> userComment = new ArrayList<>(); 
	
	private List<Link> userLinks = new ArrayList<>();
	
	private LocalDateTime lastModifiedDate;
	
	private LocalDateTime creationDate;
	
	public String getFullName() {
		String fName = Optional.ofNullable(firstName).orElse("");
		String sName = Optional.ofNullable(secondName).orElse("");
		fullName = fName.concat(" ").concat(sName);
		return fullName;
	}
	
	public static UserDTO mapUserToUserDto(User user) {
		return modelMapper.map(user, UserDTO.class);
	}

	public static User mapUserDtoToUser(UserDTO user) {
		return modelMapper.map(user, User.class);
	}
}
