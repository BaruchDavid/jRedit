package de.ffm.rka.rkareddit.config;


import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;


public class AuditorAwareImpl implements AuditorAware<String>{


	
	/**
	 * will be invoked for auditing, when trying to save anythng 
	 */
	@Override
	public Optional<String> getCurrentAuditor() {
		Optional<String> userName = Optional.empty();
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
		   userName = Optional.ofNullable(authentication.getName());
		}
		return userName;
	}
}
