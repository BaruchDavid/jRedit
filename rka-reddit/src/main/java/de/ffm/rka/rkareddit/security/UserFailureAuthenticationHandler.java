package de.ffm.rka.rkareddit.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import java.io.IOException;


public class UserFailureAuthenticationHandler  implements AuthenticationFailureHandler{

	private static final Logger LOGGER = LoggerFactory.getLogger(UserFailureAuthenticationHandler.class);
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
										AuthenticationException exception) throws IOException, ServletException {
		LOGGER.error("Authentication failed {} on request {} and queryString {} and response {}",
				exception.getMessage(), request.getRequestURL(), request.getQueryString(), response.getStatus());		
	}

}
