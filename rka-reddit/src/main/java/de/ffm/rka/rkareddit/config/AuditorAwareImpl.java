package de.ffm.rka.rkareddit.config;


import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;




public class AuditorAwareImpl implements AuditorAware<String>{
	private static final Logger LOGGER = LoggerFactory.getLogger(AuditorAwareImpl.class);

	
	/**
	 * will be invoked for auditing, when trying to save anythng 
	 */
	@Override
	public Optional<String> getCurrentAuditor() {
		Optional<String> audithUser = Optional.empty();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication != null) {
			LOGGER.info("AUDITING USER AUTHETICATION DETAILS {}", authentication.getPrincipal());
			if (!(authentication instanceof AnonymousAuthenticationToken)) {
			   audithUser = Optional.ofNullable(authentication.getName());
			}
		}else {
			LOGGER.warn("AUDITING MOCK IS USING.");
			audithUser = Optional.of("romakapt@gmx.de");
		}
		
		
		return audithUser;
	}
}
