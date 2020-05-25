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

	/**
	 * checks new password is matching of confimed new password during registration
	 */
	@Override
	public boolean isValid(UserDTO user, ConstraintValidatorContext context) {
		
		if(user.getPassword() != null && user.getNewEmail() == null) {
			return user.getPassword().equals(user.getConfirmPassword());
		} else {
			return true;
		}
		
	}
}
