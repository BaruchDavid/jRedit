package de.ffm.rka.rkareddit.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class UserFailureAuthenticationHandler  implements AuthenticationFailureHandler{

	private static final Logger LOGGER = LoggerFactory.getLogger(UserFailureAuthenticationHandler.class);
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		LOGGER.error("Authetication failed {} on request {} and queryString {} and response {}", 
				exception.getMessage(), request.getRequestURL(), request.getQueryString(), response.getStatus());		
	}

}
