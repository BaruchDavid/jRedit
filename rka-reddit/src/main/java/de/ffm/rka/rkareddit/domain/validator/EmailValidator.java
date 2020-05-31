package de.ffm.rka.rkareddit.domain.validator;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
			Optional<Authentication> authetication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
			if (authetication.isPresent()) {
				return !authetication.get().getName().equals(userDto.getNewEmail());
			} else {
				return false;
			}
			
		} else {
			return true;
		}
	}	
}
