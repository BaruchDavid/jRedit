package de.ffm.rka.rkareddit.domain.dto;


import cn.apiclub.captcha.Captcha;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.ffm.rka.rkareddit.captcha.CaptchaUtil;
import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.validator.captcha.CaptchaCheck;
import de.ffm.rka.rkareddit.domain.validator.user.UserValidationGroup.*;
import de.ffm.rka.rkareddit.domain.validator.user.email.EmailNotEqualToNewEmail;
import de.ffm.rka.rkareddit.domain.validator.user.email.IsEmailUnique;
import de.ffm.rka.rkareddit.domain.validator.user.password.CorrectPassword;
import de.ffm.rka.rkareddit.domain.validator.user.password.NewPasswordMatcher;
import de.ffm.rka.rkareddit.domain.validator.user.password.OldPasswordNewPasswordNotMatcher;
import de.ffm.rka.rkareddit.domain.validator.user.password.PasswordMatcher;
import jdk.jfr.Description;
import lombok.*;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EmailNotEqualToNewEmail(groups = {ValidationUserChangeEmail.class})
@NewPasswordMatcher(groups = {ValidationUserChangePassword.class, UnauthenticatedUserRecoverPassword.class})
@PasswordMatcher(groups = {ValidationUserRegistration.class, ValidationUserChangeEmail.class})
@CaptchaCheck(groups = {ValidationUserRegistration.class})
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private static ModelMapper modelMapper;

    static {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        modelMapper.createTypeMap(User.class, UserDTO.class).setPostConverter(context -> {
            final UserDTO destination = context.getDestination();
            if (destination.getEmail() == null) {
                destination.setEmail(StringUtils.EMPTY);
            }
            return destination;
        });
        modelMapper.addMappings(new PropertyMap<User, UserDTO>() {
            @Override
            protected void configure() {
                skip(destination.getUserComment());
                skip(destination.getUserLinks());
                skip(destination.getLastModifiedDate());

            }
        });
    }

    @Description("needs to be mapped for updating user")
    @JsonIgnore
    private Long userId;

    @IsEmailUnique(message = "A user already exists for this email", groups = {ValidationUserRegistration.class})
    @NotEmpty(message = "mail must be entered ", groups = {ValidationUserRegistration.class})
    @Size(message = "email must be between 8 and 20 signs", min = 8, max = 20, groups = {ValidationUserRegistration.class})
    @Builder.Default
    private String email = "";

    @NotEmpty(message = "mail must be entered ", groups = {ValidationUserChangeEmail.class})
    @Size(message = "email must be between 8 and 20 signs", min = 8, max = 20, groups = {ValidationUserChangeEmail.class})
    @JsonIgnore
    private String newEmail;

    @Size(message = "password must be between  5 and 20 signs", min = 5,
            max = 20, groups = {ValidationUserRegistration.class, ValidationUserChangePassword.class})
    @CorrectPassword(groups = {ValidationUserChangePassword.class, ValidationUserChangeEmail.class})
    @JsonIgnore
    private String password;

    @NotEmpty(message = "please confirm your password", groups = {ValidationUserRegistration.class})
    @JsonIgnore
    private String confirmPassword;

    @OldPasswordNewPasswordNotMatcher(groups = {ValidationUserChangePassword.class})
    @Size(message = "password must be between 5 and 20 signs",
            min = 5, max = 20, groups = {ValidationUserChangePassword.class, UnauthenticatedUserRecoverPassword.class})
    @JsonIgnore
    private String newPassword;

    @Size(message = "password must be between 5 and 20 signs",
            min = 5, max = 20, groups = {ValidationUserChangePassword.class, UnauthenticatedUserRecoverPassword.class})
    @JsonIgnore
    private String confirmNewPassword;

    @NotEmpty(message = "you must enter First Name.", groups = {ValidationChangeUserProperties.class,
            ValidationUserRegistration.class})
    @Size(min = 2, max = 10, message = "firstname must be between 2 and 10 signs", groups = {ValidationChangeUserProperties.class,
            ValidationUserRegistration.class})
    private String firstName;

    @NotEmpty(message = "you must enter Second Name.", groups = {ValidationChangeUserProperties.class,
            ValidationUserRegistration.class})
    @Size(min = 2, max = 10, message = "lastname must be between 2 and 10 signs", groups = {ValidationChangeUserProperties.class,
            ValidationUserRegistration.class})
    @Builder.Default
    private String secondName = "";

    @JsonIgnore
    private String fullName;

    @NotEmpty(message = "Please enter alias.", groups = {ValidationChangeUserProperties.class,
            ValidationUserRegistration.class})
    @Size(min = 5, message = "at least 5 characters for alias name", groups = {ValidationChangeUserProperties.class,
            ValidationUserRegistration.class})
    @Builder.Default
    private String aliasName = "";

    @JsonIgnore
    private String activationCode;

    @JsonIgnore
    private List<Comment> userComment = new ArrayList<>();

    @JsonIgnore
    private List<Link> userLinks = new ArrayList<>();

    @JsonIgnore
    private LocalDateTime lastModifiedDate;

    @JsonIgnore
    private LocalDateTime activationDeadLineDate;

    @JsonIgnore
    private LocalDateTime creationDate;

    @JsonProperty
    @Builder.Default
    private String userCreationDate = "";


    @Transient
    private String captcha;

    @Transient
    private String hiddenCaptcha;

    @Transient
    private String realCaptcha;


    public String getFullName() {
        String fName = Optional.ofNullable(firstName).orElse("");
        String sName = Optional.ofNullable(secondName).orElse("");
        fullName = fName.concat(" ").concat(sName);
        return fullName;
    }

    public static UserDTO mapUserToUserDto(User user) {
        final UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        final String creationDate = Optional.ofNullable(userDTO.getCreationDate())
                .map(LocalDateTime::toString)
                .orElse("");
        userDTO.setUserCreationDate(creationDate);
        return userDTO;
    }

    public static User mapUserDtoToUser(UserDTO user) {
        return modelMapper.map(user, User.class);
    }

    public static void createCaptcha(UserDTO user) {
        Captcha captcha = CaptchaUtil.createCaptcha(240, 70);
        user.setHiddenCaptcha(captcha.getAnswer());
        user.setCaptcha("");
        user.setRealCaptcha(CaptchaUtil.encodeCaptcha(captcha));

    }
}
