package de.ffm.rka.rkareddit.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GlobalAccessDeniedHandler implements AccessDeniedHandler{

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalAccessDeniedHandler.class);
	private static final String LOGIN = "login";
	private static final String REGISTER = "registration";
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException {
		 Authentication auth = SecurityContextHolder.getContext().getAuthentication();
       if (auth != null) {
    	   LOGGER.warn("USER: {} WITH ADDRESS {} ATTEMPTED ILLEGAL ACCEsSS TO PROTECTED URL: {} ",auth.getName(), request.getRemoteAddr(), request.getRequestURI());
    	   if(request.getRequestURI().contains(LOGIN)
    		 || request.getRequestURI().contains(REGISTER)) {
    		   response.sendRedirect(request.getContextPath().concat("/links"));
    	   } else {
    		   response.sendRedirect(request.getContextPath().concat("/error/accessDenied"));
    	   }
       }
	}

}
