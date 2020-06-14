package de.ffm.rka.rkareddit.domain.validator.user;

import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
		if(userDto.getNewEmail() != null) {
			return !userDto.getEmail().equals(userDto.getNewEmail());
		} else {
			return false;
		}
	}	
}
