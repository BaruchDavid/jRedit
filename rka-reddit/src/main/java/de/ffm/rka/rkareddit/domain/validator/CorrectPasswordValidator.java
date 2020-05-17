package de.ffm.rka.rkareddit.domain.validator;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * checks if users current password is correct
 *
 */
public class CorrectPasswordValidator implements ConstraintValidator<CorrectPassword, String>, BCryptPwEncoderManager{
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return matches(value);
	}

	
}
