package de.ffm.rka.rkareddit.domain.validator;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.util.BeanUtil;

public interface BCryptPwEncoderManager {

	default boolean matches(final String comparedPw) {
		Optional<Authentication> authetication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
		if(!authetication.isEmpty()) {
			BCryptPasswordEncoder encoder = BeanUtil.getBeanFromContext(BCryptPasswordEncoder.class);
			User user = (User) authetication.get().getPrincipal();
			return encoder.matches(comparedPw, user.getPassword());
			
		} else {
			return false;
		}
	}
	
}
