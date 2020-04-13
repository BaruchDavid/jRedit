package de.ffm.rka.rkareddit.security;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.ui.Model;

import de.ffm.rka.rkareddit.controller.LinkController;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.exception.UserAuthenticationLostException;
import de.ffm.rka.rkareddit.service.UserService;

public class UserSuccessfullAthenticationHandler implements AuthenticationSuccessHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserSuccessfullAthenticationHandler.class);
	
	@Autowired
	private UserService userService;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
//		Optional<HttpSession> session = Optional.ofNullable(request.getSession());
//		if (session.isPresent()) {
//			HttpSession validSession = session.get();
//			String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//			Optional<User> user = userService.findUserById(userName);
//			if(user.isPresent()) {
//				validSession.setAttribute("user",user.get());
//			} else {
//				try {
//					throw new UserAuthenticationLostException("NO USER WITHIN SESSION");
//				} catch (UserAuthenticationLostException e) {
//					LOGGER.info("{} USER NOT FOUND AFTER LOGIN FOR SESSION SETTING", userName);
//				}
//			}
//			
//			
//		}
		response.sendRedirect(request.getContextPath().concat("/links"));
	}

}
