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
public class EmailValidator implements ConstraintValidator<EmailNotEqualToNewEmail, UserDTO>{

	/**
	 * checks if current email is not equal to new email during email changing
	 */
	@Override
	public boolean isValid(UserDTO userDto, ConstraintValidatorContext context) {
		if(userDto.getNewEmail() != null) {
			return !userDto.getEmail().equals(userDto.getNewEmail());
		} else {
			return true;
		}
	}	
}
