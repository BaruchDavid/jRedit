package de.ffm.rka.rkareddit.exception;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ContextPathCompositeHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import de.ffm.rka.rkareddit.controller.AuthController;
import de.ffm.rka.rkareddit.domain.User;

/**
 * This controlleradvice works only on @Controller-Classes, not
 * on @RestController-Classes
 * 
 * @author kaproma
 *
 */
@ControllerAdvice(basePackages = {"de.ffm.rka.rkareddit.controller"})
class GlobalDefaultExceptionHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
	public static final String DEFAULT_ERROR_VIEW = "error/userAuth";
	public static final String PAGE_NOT_FOUND = "error/pageNotFound";

	/**
	 * Exception handling for controller If the exception is annotated
	 * with @ResponseStatus rethrow it and let the framework handle it - like the
	 * OrderNotFoundException example at the start of this post. AnnotationUtils is
	 * a Spring Framework utility class.
	 */
	@ExceptionHandler(value = { UserAuthenticationLostException.class, NullPointerException.class,IllegalArgumentException.class })
	public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
		
		return internalControllerError(req, e);
	}

	private ModelAndView internalControllerError(HttpServletRequest req, Exception e) throws Exception {
		if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null)
			throw e;

		// Otherwise setup and send the user to a default error-view.
		ModelAndView mav = new ModelAndView();
		mav.addObject("user", new User());
		mav.addObject("exception", e);
		mav.addObject("url", req.getRequestURL());
		mav.setViewName(DEFAULT_ERROR_VIEW);
		return mav;
	}
	
	
	
	
	@ExceptionHandler(value = { IllegalAccessException.class })
	public ModelAndView userError(HttpServletRequest req, Exception e) throws Exception {
		ModelAndView mav = new ModelAndView();
		Optional<String> userDetails = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getName());
		if(userDetails.isPresent()) {
			//ToDo: dear user, page has not been found
		}else {
			//ToDo: dear visitor, page has not been found
		}
		mav.addObject("user", new User());
		mav.addObject("exception", e);
		mav.addObject("url", req.getRequestURL());
		mav.setViewName(DEFAULT_ERROR_VIEW);
		return internalControllerError(req, e);
	}
}