package de.ffm.rka.rkareddit.domain.validator.user;


import de.ffm.rka.rkareddit.domain.validator.BCryptPwEncoderManager;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * validates old password not equal to new password
 * @author roman.kaptsan
 *
 */
public class OldPasswordNewPasswordValidator implements ConstraintValidator<OldPasswordNewPasswordNotMatcher, String>,
														BCryptPwEncoderManager {

	@Override
	public boolean isValid(String newPw, ConstraintValidatorContext context) {
		return !passwordAndPwConfirmationMatches(newPw);
	}

}
