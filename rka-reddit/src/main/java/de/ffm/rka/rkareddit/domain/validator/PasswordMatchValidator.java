package de.ffm.rka.rkareddit.domain.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;

/**
 * 
 * validates given constraint for given object
 * @author RKA
 *
 */
public class PasswordMatchValidator implements ConstraintValidator<PasswordMatcher, UserDTO>{

	@Override
	public boolean isValid(UserDTO user, ConstraintValidatorContext context) {
		
		return user.getPassword().equals(user.getConfirmPassword());
	}

}
