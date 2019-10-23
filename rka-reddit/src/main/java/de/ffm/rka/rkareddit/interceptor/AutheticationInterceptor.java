package de.ffm.rka.rkareddit.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import de.ffm.rka.rkareddit.exception.UserAuthenticationLostException;
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
            	if("anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            		LOGGER.warn("autheticated user could not access method with authetication");
            		LOGGER.warn("Browser-Info {}", request.getHeader("user-agent"));
            		LOGGER.warn("IP-Adresse {}", request.getHeader("True-Client-IP"));
            		LOGGER.warn("Remote Address {}", request.getRemoteAddr());  	
            		//throw new UserAuthenticationLostException("LOST AUTHENTICATION-CONTEXT");
            	}
            }
        }
		return super.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if(HttpStatus.NOT_FOUND.value()==response.getStatus()) {
			throw new IllegalAccessException(String.valueOf(response.getStatus()));
		}
		super.postHandle(request, response, handler, modelAndView);
	}
	
	
}
