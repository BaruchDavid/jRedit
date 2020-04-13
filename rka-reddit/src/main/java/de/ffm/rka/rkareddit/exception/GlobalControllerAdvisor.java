package de.ffm.rka.rkareddit.exception;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

import de.ffm.rka.rkareddit.controller.AuthController;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;

/**
 * This controlleradvice works only on @Controller-Classes, not
 * on @RestController-Classes
 * 
 * @author kaproma
 *
 */
@ControllerAdvice(basePackages = {"de.ffm.rka.rkareddit.controller"})
public class GlobalControllerAdvisor {
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerAdvisor.class);
	public static final String USER_ERROR_VIEW = "error/application";
	public static final String DEFAULT_APPLICATION_ERROR = "error/basicError";
	public static final String ANONYMOUS = "anonymousUser";

	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@ExceptionHandler(value = { UserAuthenticationLostException.class, NullPointerException.class,IllegalArgumentException.class,
			IllegalAccessException.class, NumberFormatException.class, Exception.class})
	public ModelAndView defaultErrorHandler(HttpServletRequest req, HttpServletResponse res, Exception exception) throws Exception {
		Optional<Authentication> authetication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
		User user = new User();	
		String view = USER_ERROR_VIEW;
		String visitorName="";
		if(authetication.isPresent()) {
			visitorName = authetication.get().getName();
			if(!ANONYMOUS.equals(visitorName)) { 
				user = (User) userDetailsService.loadUserByUsername(visitorName);	
			}else { 
				user.setFirstName("dear visitor");	
			}
		} else {
			user.setFirstName("dear visitor");
		}
		LOGGER.error("EXCEPTION ACCURED: {} FOR USER {} ON REQUESTED URL {} {}", exception.getMessage(), 
																			visitorName, 
																			req.getMethod(),
																			req.getRequestURL());

		switch (getExceptionName(exception.getClass().getCanonicalName())) {
		case "MethodArgumentTypeMismatchException":
		case "IllegalArgumentException":
			view = DEFAULT_APPLICATION_ERROR;
			res.setStatus(404);
			break;
		case "NullPointerException":
		case "UserAuthenticationLostException":
			res.setStatus(500);
			view = USER_ERROR_VIEW;
			break;
		default:
			res.setStatus(500);
			view = USER_ERROR_VIEW;
			break;
		}

		return createErrorView(req.getRequestURL().toString(),user, view);
	}

	private ModelAndView createErrorView(String req, User user, String errorView) throws Exception {
		
		ModelAndView mav = new ModelAndView();
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