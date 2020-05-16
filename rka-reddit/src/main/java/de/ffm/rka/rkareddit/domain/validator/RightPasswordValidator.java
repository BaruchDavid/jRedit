package de.ffm.rka.rkareddit.domain.validator;


import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.util.BeanUtil;

public class RightPasswordValidator implements ConstraintValidator<CorrectPassword, String>{
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		Optional<Authentication> authetication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
		if(!authetication.isEmpty()) {
			BCryptPasswordEncoder encoder = BeanUtil.getBeanFromContext(BCryptPasswordEncoder.class);
			User user = (User) authetication.get().getPrincipal();
			return encoder.matches(value, user.getPassword());
			
		} else {
			return false;
		}
		
	}

	
}
