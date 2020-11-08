package de.ffm.rka.rkareddit.domain.validator.user;

import de.ffm.rka.rkareddit.domain.dto.UserDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

/**
 * 
 * validates given constraint for given object
 * @author RKA
 *
 */
public class EmailToNewEmailValidator implements ConstraintValidator<EmailNotEqualToNewEmail, UserDTO>{

	/**
	 * when newEmail is not empty, so check this with current email
	 * otherwise is newEmail empty and return false anyway
	 */
	@Override
	public boolean isValid(UserDTO userDto, ConstraintValidatorContext context) {
		String oldMail = Optional.ofNullable(userDto.getEmail()).orElse("");
		String newMail = Optional.ofNullable(userDto.getNewEmail()).orElse("");
		if(oldMail.isEmpty() || newMail.isEmpty()) {
			return false;
		} else {
			return !oldMail.equals(newMail);	
		}

	}	
}
