package de.ffm.rka.rkareddit.domain.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import de.ffm.rka.rkareddit.domain.User;

/**
 * validates given constraint for given object
 * @author RKA
 *
 */
public class PasswordValidator implements ConstraintValidator<PasswordMatcher, User>{

	@Override
	public boolean isValid(User user, ConstraintValidatorContext context) {
		
		return user.getPassword().equals(user.getConfirmPassword());
	}

}
