package de.ffm.rka.rkareddit.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import de.ffm.rka.rkareddit.domain.User;

/**
 * This controlleradvice works only on @Controller-Classes, not on @RestController-Classes
 * @author kaproma
 *
 */
@ControllerAdvice
class GlobalDefaultExceptionHandler {
  public static final String DEFAULT_ERROR_VIEW = "error/userAuth";

  /**
   * Exception handling for controller
   *  If the exception is annotated with @ResponseStatus rethrow it and let
   *  the framework handle it - like the OrderNotFoundException example
   *  at the start of this post.
   *  AnnotationUtils is a Spring Framework utility class.
   */
  @ExceptionHandler(value = {UserAuthenticationLostException.class, NullPointerException.class, IllegalArgumentException.class})
  public ModelAndView
  defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
    
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
}