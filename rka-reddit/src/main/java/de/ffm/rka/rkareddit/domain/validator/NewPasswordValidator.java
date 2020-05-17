package de.ffm.rka.rkareddit.domain.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import de.ffm.rka.rkareddit.domain.dto.UserDTO;

/** new password and new password confirmation should be equal */
public class NewPasswordValidator implements ConstraintValidator<NewPasswordMatcher, UserDTO>{

	@Override
	public boolean isValid(UserDTO user, ConstraintValidatorContext context) {
		
		return user.getNewPassword().equals(user.getConfirmNewPassword());
	}

}
