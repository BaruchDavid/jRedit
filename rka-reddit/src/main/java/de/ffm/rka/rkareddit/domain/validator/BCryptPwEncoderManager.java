package de.ffm.rka.rkareddit.domain.validator;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.util.BeanUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

public interface BCryptPwEncoderManager {

	/**
	 * 1. will be called from register function when authetication is present of registered user
	 * 2. will be called from register function when trying to register
	 * 3. all else cases
	 * @param comparedPw is a new pw
	 * @return true for equals pw or false
	 */
	default boolean passwordAndPwConfirmationMatches(final String comparedPw) {
		Optional<Authentication> authetication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
		return authetication.map(authenticated -> {
								BCryptPasswordEncoder encoder = BeanUtil.getBeanFromContext(BCryptPasswordEncoder.class);
								User user = (User) authetication.get().getPrincipal();
								return encoder.matches(comparedPw, user.getPassword());
							})
							.orElse(false);
	}
}
