package de.ffm.rka.rkareddit.domain.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.validator.user.*;
import de.ffm.rka.rkareddit.domain.validator.user.UserValidationgroup.ValidationChangeUserProperties;
import de.ffm.rka.rkareddit.domain.validator.user.UserValidationgroup.ValidationUserChangeEmail;
import de.ffm.rka.rkareddit.domain.validator.user.UserValidationgroup.ValidationUserChangePassword;
import de.ffm.rka.rkareddit.domain.validator.user.UserValidationgroup.ValidationUserRegistration;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
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
	@Builder.Default
	private String email="";
	
	@NotEmpty(message = "mail must be entered ", groups = {ValidationUserChangeEmail.class})
	@Size(message = "email must be between 8 and 20 signs",min = 8, max = 20, groups = {ValidationUserChangeEmail.class})
	@JsonIgnore
	private String newEmail;
	
	@Size(message = "password must be between  5 and 20 signs",min = 5,
			max = 20, groups = {ValidationUserRegistration.class, ValidationUserChangePassword.class})
	@CorrectPassword(groups = {ValidationUserChangePassword.class, ValidationUserChangeEmail.class})
	@JsonIgnore
	private String password;
	
	@NotEmpty(message = "please confirm your password", groups = {ValidationUserRegistration.class})
	@JsonIgnore
	private String confirmPassword;
	
	@OldPasswordNewPasswordNotMatcher(groups = {ValidationUserChangePassword.class})
	@Size(message = "password must be between 5 and 20 signs",
			min = 5, max = 20, groups = {ValidationUserChangePassword.class})
	@JsonIgnore
	private String newPassword;
	
	@Size(message = "password must be between 5 and 20 signs",
			min = 5, max = 20, groups = {ValidationUserChangePassword.class})
	@JsonIgnore
	private String confirmNewPassword;
	
	@NotEmpty(message = "you must enter First Name.", groups = {ValidationChangeUserProperties.class,
																ValidationUserRegistration.class})
	private String firstName;
	
	@NotEmpty(message = "you must enter Second Name.", groups = {ValidationChangeUserProperties.class,
																ValidationUserRegistration.class})
	@Builder.Default
	private String secondName="";
	
	@JsonIgnore
	private  String fullName;
	
	@NotEmpty(message = "Please enter alias.", groups = {ValidationChangeUserProperties.class,
															ValidationUserRegistration.class})
	@Size(min = 5, message = "at least 5 characters for alias name", groups = {ValidationChangeUserProperties.class,
																				ValidationUserRegistration.class})
	@Builder.Default
	private  String aliasName="";

	@JsonIgnore
	private String activationCode;

	@JsonIgnore
	private List<Comment> userComment = new ArrayList<>(); 

	@JsonIgnore
	private List<Link> userLinks = new ArrayList<>();

	@JsonIgnore
	private LocalDateTime lastModifiedDate;

	@JsonIgnore
	private LocalDateTime creationDate;

	@JsonProperty
	@Builder.Default
	private String userCreationDate="";

	
	public String getFullName() {
		String fName = Optional.ofNullable(firstName).orElse("");
		String sName = Optional.ofNullable(secondName).orElse("");
		fullName = fName.concat(" ").concat(sName);
		return fullName;
	}
	
	public static UserDTO mapUserToUserDto(User user) {
		final UserDTO userDTO = modelMapper.map(user, UserDTO.class);
		final String creationDate = Optional.ofNullable(userDTO.getCreationDate())
											.map(localDateTime -> localDateTime.toString())
											.orElse("");
		userDTO.setUserCreationDate(creationDate);
		return userDTO;
	}

	public static User mapUserDtoToUser(UserDTO user) {
		return modelMapper.map(user, User.class);
	}
}
