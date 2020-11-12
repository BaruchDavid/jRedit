package de.ffm.rka.rkareddit.domain.validator.user;


import de.ffm.rka.rkareddit.domain.validator.BCryptPwEncoderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

/**
 * checks if users current password is correct
 *
 */
public class CorrectPasswordValidator implements ConstraintValidator<CorrectPassword, String>, BCryptPwEncoderManager {
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return this.matches(value);
	}

	/**
	 * must return true when calling from register-method
	 * otherwise it will be invoked from email changing function
	 */
	@Override
	public boolean matches(String comparedPw) {
		Optional<Authentication> authentication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
		if(!authentication.isEmpty() && "anonymousUser".equals(authentication.get().getPrincipal())) {
			return true;
		} else {
			return BCryptPwEncoderManager.super.matches(comparedPw);
		}
		
	}	
	
}
