package de.ffm.rka.rkareddit.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import de.ffm.rka.rkareddit.config.DatabaseLoader;

public class UserFailureAuthenticationHandler  implements AuthenticationFailureHandler{

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseLoader.class);
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		LOGGER.error("Authetication failed {} on request {} and queryString {} and response {}", 
				exception.getMessage(), request.getRequestURL(), request.getQueryString(), response.getStatus());
//	    Map<String, String> failureUrlMap = new HashMap<>();
//	    failureUrlMap.put(BadCredentialsException.class.getName(), LoginAuthenticationFailureHandler.PASS_ERROR_URL);
//	    failureUrlMap.put(CaptchaException.class.getName(), LoginAuthenticationFailureHandler.CODE_ERROR_URL);
//	    failureUrlMap.put(AccountExpiredException.class.getName(), LoginAuthenticationFailureHandler.EXPIRED_URL);
//	    failureUrlMap.put(LockedException.class.getName(), LoginAuthenticationFailureHandler.LOCKED_URL);
//	    failureUrlMap.put(DisabledException.class.getName(), LoginAuthenticationFailureHandler.DISABLED_URL);
//	    failureHandler.setExceptionMappings(failureUrlMap);
//		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		//response.sendRedirect("/jReditt/userAuth");
		
	}

}
