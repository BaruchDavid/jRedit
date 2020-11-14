package de.ffm.rka.rkareddit.exception;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;
import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * This controller advice works only on @Controller-Classes, not
 * on @RestController-Classes
 * 
 * @author kaproma
 *
 */

@ControllerAdvice
public class GlobalControllerAdvisor {
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerAdvisor.class);
	public static final String USER_ERROR_VIEW = "error/application";
	public static final String DEFAULT_APPLICATION_ERROR = "error/basicError";
	public static final String PAGE_NOT_FOUND = "error/pageNotFound";
	public static final String ANONYMOUS_USER = "anonymousUser";
	public static final String ANONYMOUS = "anonymous";

	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@ExceptionHandler(value = { HttpRequestMethodNotSupportedException.class, UserAuthenticationLostException.class, NullPointerException.class,
								IllegalArgumentException.class, IllegalAccessException.class,
								NumberFormatException.class, ServiceException.class, Exception.class})
	public ModelAndView defaultErrorHandler(HttpServletRequest req, HttpServletResponse res, Exception exception){
		Optional<Authentication> authentication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
		UserDTO user = new UserDTO();	
		String view = USER_ERROR_VIEW;
		String visitorName="";
		if(authentication.isPresent()) {
			visitorName = authentication.get().getName();
			if(!ANONYMOUS.equals(visitorName) && !ANONYMOUS_USER.equals(visitorName) ) {				
				user = UserDTO.mapUserToUserDto((User) userDetailsService.loadUserByUsername(visitorName));
			}else { 
				user.setFirstName("guest");	
			}
		} else {
			user.setFirstName("guest");
		}
		final String exceptionType = getExceptionName(exception.getClass().getCanonicalName());
		LOGGER.error("EXCEPTION {} OCCURRED: MESSAGE {} FOR USER {} ON REQUESTED URL {} {}", exceptionType, exception.getMessage(),
																			visitorName, 
																			req.getMethod(),
																			req.getRequestURL());

		switch (exceptionType) {
			case "MissingServletRequestParameterException":
			case "ValidationException":
			case "MethodArgumentTypeMismatchException":
			case "IllegalArgumentException":
			case "NullPointerException":
			case "NumberFormatException":
				view = DEFAULT_APPLICATION_ERROR;
				res.setStatus(HttpStatus.SC_BAD_REQUEST);
				break;
			case "UserAuthenticationLostException":
			case "AuthenticationCredentialsNotFoundException":
			case "UsernameNotFoundException":
				res.setStatus(HttpStatus.SC_UNAUTHORIZED);
				break;
			case "AccessDeniedException":
				res.setStatus(HttpStatus.SC_FORBIDDEN);
				break;
			case "HttpRequestMethodNotSupportedException":
				view = PAGE_NOT_FOUND;
				res.setStatus(HttpStatus.SC_METHOD_NOT_ALLOWED);
				break;
			case "ServiceException":
				view = DEFAULT_APPLICATION_ERROR;
				res.setStatus(HttpStatus.SC_NOT_FOUND);
				break;
			default:
				res.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
				break;
		}

		return createErrorView(req.getRequestURL().toString(),user, view);
	}

	private ModelAndView createErrorView(String req, UserDTO user, String errorView) {
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("userDto", user);
		mav.addObject("user", user);
		mav.addObject("url", req);
		mav.setViewName(errorView);
		return mav;
	}

	/**
	 * determines exception name of full qualified name
	 */
	private String getExceptionName(final String canonicalExcName) {
		int exceptionIndex=canonicalExcName.split("\\.").length;
		return canonicalExcName.split("\\.")[exceptionIndex-1];
	}
}