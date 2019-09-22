package de.ffm.rka.rkareddit.interceptor;

import java.lang.reflect.Parameter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import de.ffm.rka.rkareddit.security.SecConfig;

/**
 * purpose of this class is checking of autheticated users by using
 * annotation @AuthenticationPrincipal
 * 
 * @author rka
 *
 */
public class AutheticationInterceptor extends HandlerInterceptorAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecConfig.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
          
            if(handlerMethod.getMethod().getParameters()[0].getAnnotation(AuthenticationPrincipal.class) instanceof AuthenticationPrincipal) {

            	if(SecurityContextHolder.getContext().getAuthentication().getName().isEmpty()) {
            		LOGGER.warn("autheticated user could not access method with authetication");
            		LOGGER.warn("Browser-Info {}", request.getHeader("user-agent"));
            		LOGGER.warn("IP-Adresse {}", request.getHeader("True-Client-IP"));
            		LOGGER.warn("Remote Address {}", request.getRemoteAddr());  		
            		response.sendRedirect("error/userAuth");
            	}
            }else {
            	LOGGER.info("ist nicht drin");
            }
        }

		return super.preHandle(request, response, handler);
	}

	/**
	 * request absenden, nach dem das Profil geladen wurde um Avatar und Info nachzuladen.
	 * Wird den fetch-Request im Frontend damit ersetzen.
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		super.afterCompletion(request, response, handler, ex);
	}
	
	

}
