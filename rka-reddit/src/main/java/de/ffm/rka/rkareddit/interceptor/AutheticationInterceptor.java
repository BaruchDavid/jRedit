package de.ffm.rka.rkareddit.interceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.NullArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import de.ffm.rka.rkareddit.domain.User;
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
	private static final String IS_404_ERROR ="404";
	private static final String IS_400_ERROR ="400";
	public static final String ANONYMOUS = "anonymousUser";
	public static final String PRIVATE_PROFILE_URL = "private";

	/**
	 * any method with @AutheticationPrincipal and without @Secured
	 * @throws Exception 
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		LOGGER.info("current URL in preHandle {}", request.getRequestURL());
		
		if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();
            if(method.getParameters()[0].getAnnotation(AuthenticationPrincipal.class) instanceof AuthenticationPrincipal
            	&& PRIVATE_PROFILE_URL.contains(request.getRequestURL())) {
            		LOGGER.info("METHODE: "+method.getName());
            		LOGGER.warn("autheticated user could not access method with authetication");
            		LOGGER.warn("Browser-Info {}", request.getHeader("user-agent"));
            		LOGGER.warn("IP-Adresse {}", request.getHeader("True-Client-IP"));
            		LOGGER.warn("Remote Address {}", request.getRemoteAddr());  	
            		throw new UserAuthenticationLostException("LOST AUTHENTICATION-CONTEXT");
            } else if (String.valueOf(response.getStatus()).startsWith(IS_404_ERROR)
            		|| (String.valueOf(response.getStatus()).startsWith(IS_400_ERROR))) {
    			LOGGER.info("PAGE NOT FOUND:  {} with Status: {}", request.getRequestURL(), response.getStatus()); 
    		}     		
        }
		return super.preHandle(request, response, handler);
	}
	
	public static List<String> getRequestHeaderList(HttpServletRequest request) {
		Enumeration<String> headerNames = request.getHeaderNames();
		List<String> resultList;
		if (headerNames == null || !headerNames.hasMoreElements()) {
			return Collections.emptyList();
		}
		resultList = new ArrayList<String>();
		while (headerNames != null && headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement().toString();
			String headerValue = "";
			Enumeration<String> header = request.getHeaders(headerName);
			while (header != null && header.hasMoreElements()) {
				headerValue = headerValue + "," + header.nextElement().toString();
			}
			if (headerValue.length() > 0) {
				headerValue = headerValue.substring(1, headerValue.length());
			}
			resultList.add(headerName + "=" + headerValue);
		}
		return resultList;
	}


	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if(String.valueOf(response.getStatus()).startsWith(IS_404_ERROR)) {
			LOGGER.info("PAGE NOT FOUND:  {} with Status: {}", request.getRequestURL(), response.getStatus()); 
		}else {
			super.postHandle(request, response, handler, modelAndView);
		}
	}
}
