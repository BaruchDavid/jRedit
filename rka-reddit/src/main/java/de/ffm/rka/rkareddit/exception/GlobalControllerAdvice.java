package de.ffm.rka.rkareddit.exception;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.ffm.rka.rkareddit.controller.AuthController;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;

@ControllerAdvice(basePackages = {"de.ffm.rka.rkareddit.controller"})
public class GlobalControllerAdvice {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
	public static final String DEFAULT_ERROR_VIEW = "error/userAuth";
	public static final String PAGE_NOT_FOUND = "error/pageNotFound";

	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@ExceptionHandler(value = { UserAuthenticationLostException.class, NullPointerException.class,IllegalArgumentException.class })
	public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception exception) throws Exception {
		Optional<String> userDetails = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getName());
		User user = new User();
		if(!"anonymousUser".equals(userDetails.get())) { 
			user = (User) userDetailsService.loadUserByUsername(userDetails.get());
			LOGGER.info("NOT FOUND REQUESTED PAGE: {} FROM USER {}", req.getRequestURL().toString(), user);
		}else { 
			user.setFirstName("visitor");
		}
		
		if(exception instanceof MethodArgumentTypeMismatchException) {
			return internalControllerError(req.getRequestURL().toString(),user, PAGE_NOT_FOUND);
		}else {
			return internalControllerError(req.getRequestURL().toString(),user, DEFAULT_ERROR_VIEW);
		}
		
		
	}
	
	@ExceptionHandler(value = { IllegalAccessException.class })
	public ModelAndView userError(HttpServletRequest req, Exception e) throws Exception {
		Optional<String> userDetails = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getName());
		User user = new User();
		if(userDetails.isPresent()) { 
			user = (User) userDetailsService.loadUserByUsername(userDetails.get());
			LOGGER.info("NOT FOUND REQUESTED PAGE: {} FROM USER {}", req.getRequestURL().toString(), user);
		}else { 
			user.setFirstName("visitor");
		}
		return internalControllerError(req.getRequestURL().toString(), user, PAGE_NOT_FOUND);
	}

	private ModelAndView internalControllerError(String req, User user, String errorView) throws Exception {
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("user", user);
		mav.addObject("url", req);
		mav.setViewName(errorView);
		return mav;
	}
}
