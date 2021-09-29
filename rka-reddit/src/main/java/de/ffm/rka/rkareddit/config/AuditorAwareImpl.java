package de.ffm.rka.rkareddit.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;




public class AuditorAwareImpl implements AuditorAware<String>{
	private static final Logger LOGGER = LoggerFactory.getLogger(AuditorAwareImpl.class);

	
	/**
	 * will be invoked for auditing, when trying to save anything
	 */
	@Override
	public Optional<String> getCurrentAuditor() {
		Optional<String> authenticatedUser = Optional.empty();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication != null) {
			LOGGER.info("AUDITING USER AUTHENTICATION DETAILS {}", authentication.getPrincipal());
			if (!(authentication instanceof AnonymousAuthenticationToken)) {
				authenticatedUser = Optional.ofNullable(authentication.getName());
			}
		}else {
			LOGGER.warn("AUDITING MOCK IS USING.");
			authenticatedUser = Optional.of("kaproma@yahoo.de");
		}
		return authenticatedUser;
	}
}
