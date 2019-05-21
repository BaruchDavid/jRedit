package de.ffm.rka.rkareddit.config;


import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import de.ffm.rka.rkareddit.domain.User;


public class AuditorAwareImpl implements AuditorAware<String>{

	/**
	 * will be invoked for auditing, when trying to save anythng 
	 */
	@Override
	public Optional<String> getCurrentAuditor() {
		Optional<String> userName = Optional.of(((User)SecurityContextHolder.getContext()
				 .getAuthentication()
				 .getPrincipal()).getEmail());
		return userName;
	}
}
