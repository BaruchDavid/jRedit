package de.ffm.rka.rkareddit.exception;



import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * This controlleradvice works only on @REstController-Classes, not on @Controller-Classes
 * @author kaproma
 *
 */
@RestControllerAdvice(basePackages = {"de.ffm.rka.rkareddit.rest.controller"})
class GlobalRESTExceptionHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalRESTExceptionHandler.class);
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR,reason="information not availible")
	@ResponseBody
	@ExceptionHandler(value = {UserAuthenticationLostException.class, NullPointerException.class, IllegalArgumentException.class})
	public List<String> internalError(HttpServletRequest req, Exception err){
		LOGGER.error("REST CONTROLLER EXCEPTION {} {} on: {}", err.getMessage(), req.getMethod(), req.getRequestURL());
		List<String> informations = new ArrayList<String>();
		informations.add("fehler1");
		informations.add("fehler2");
		informations.add("fehler3");
		informations.add("fehler4");
		return  informations;
		
	}
}