package de.ffm.rka.rkareddit.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import de.ffm.rka.rkareddit.service.UserService;

public class UserSuccessfullAthenticationHandler implements AuthenticationSuccessHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserSuccessfullAthenticationHandler.class);
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		LOGGER.info("Successfully autheticated {}", authentication.getName());
		response.sendRedirect(request.getContextPath().concat("/links"));
	}

}
