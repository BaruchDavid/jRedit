package de.ffm.rka.rkareddit.domain.validator;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.util.BeanUtil;

public interface BCryptPwEncoderManager {

	/**
	 * 1. will be called from register function when authetication is present of registered user
	 * 2. will be called from register function when trying to register
	 * 3. all else cases
	 * @param comparedPw
	 * @return
	 */
	default boolean matches(final String comparedPw) {
		Optional<Authentication> authetication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
		if(!authetication.isEmpty() && !"anonymousUser".equals(authetication.get().getPrincipal())) {
			BCryptPasswordEncoder encoder = BeanUtil.getBeanFromContext(BCryptPasswordEncoder.class);
			User user = (User) authetication.get().getPrincipal();
			return encoder.matches(comparedPw, user.getPassword());
		} else {
			return false;
		}
	}
}
