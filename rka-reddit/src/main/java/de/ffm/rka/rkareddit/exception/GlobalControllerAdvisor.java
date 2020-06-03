package de.ffm.rka.rkareddit.exception;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;

/**
 * This controlleradvice works only on @Controller-Classes, not
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
	private ModelMapper modelMapper;

	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@ExceptionHandler(value = { HttpRequestMethodNotSupportedException.class, UserAuthenticationLostException.class, NullPointerException.class,IllegalArgumentException.class,
			IllegalAccessException.class, NumberFormatException.class, ServiceException.class, Exception.class})
	public ModelAndView defaultErrorHandler(HttpServletRequest req, HttpServletResponse res, Exception exception){
		Optional<Authentication> authetication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
		UserDTO user = new UserDTO();	
		String view = USER_ERROR_VIEW;
		String visitorName="";
		if(authetication.isPresent()) {
			visitorName = authetication.get().getName();
			if(!ANONYMOUS.equals(visitorName) && !ANONYMOUS_USER.equals(visitorName) ) { 
				user = modelMapper.map((User) userDetailsService.loadUserByUsername(visitorName), UserDTO.class);	
			}else { 
				user.setFirstName("guest");	
			}
		} else {
			user.setFirstName("guest");
		}
		final String exceptionType = getExceptionName(exception.getClass().getCanonicalName());
		LOGGER.error("EXCEPTION {} ACCURED: MESSAGE {} FOR USER {} ON REQUESTED URL {} {}", exceptionType, exception.getMessage(), 
																			visitorName, 
																			req.getMethod(),
																			req.getRequestURL());

		switch (exceptionType) {
		case "ValidationException":
		case "MethodArgumentTypeMismatchException":
		case "IllegalArgumentException":
		case "NullPointerException":
			view = DEFAULT_APPLICATION_ERROR;
			res.setStatus(400);
			break;
		case "UserAuthenticationLostException":
		case "AuthenticationCredentialsNotFoundException":
		case "UsernameNotFoundException":
			res.setStatus(401);
			break;		
		case "AccessDeniedException":
			res.setStatus(403);
			break;
		case "HttpRequestMethodNotSupportedException":
			view = PAGE_NOT_FOUND;
			res.setStatus(404);
			break;
		case "ServiceException":
			view = DEFAULT_APPLICATION_ERROR;
			res.setStatus(504);
			break;	
		default:
			res.setStatus(500);
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
	 * determines exceptionname of fullqualified name
	 */
	private String getExceptionName(final String canonicalExcName) {
		int exceptionIndex=canonicalExcName.split("\\.").length;
		return canonicalExcName.split("\\.")[exceptionIndex-1];
	}
}