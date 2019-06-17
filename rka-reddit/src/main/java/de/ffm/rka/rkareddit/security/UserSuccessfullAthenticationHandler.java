package de.ffm.rka.rkareddit.security;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.service.UserService;

public class UserSuccessfullAthenticationHandler implements AuthenticationSuccessHandler {

	@Autowired
	private UserService userService;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		Optional<HttpSession> session = Optional.ofNullable(request.getSession());
		if (session.isPresent()) {
			HttpSession validSession = session.get();
			String userName = SecurityContextHolder.getContext().getAuthentication().getName();
			Optional<User> user = userService.findUserById(userName);
			validSession.setAttribute("user", user.get());
			response.sendRedirect(request.getContextPath().concat("/"));
		}
	}

}
