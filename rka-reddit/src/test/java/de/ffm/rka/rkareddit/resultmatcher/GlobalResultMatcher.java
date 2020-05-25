package de.ffm.rka.rkareddit.resultmatcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.ModelResultMatchers;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import de.ffm.rka.rkareddit.controller.AuthController;

public class GlobalResultMatcher extends ModelResultMatchers {
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalResultMatcher.class);
	
	public static GlobalResultMatcher globalErrors() {
        return new GlobalResultMatcher();
    }
	
	public ResultMatcher hasGlobalError(String attribute, String expectedMessage) {
        return result -> {
            BindingResult bindingResult = getBindingResult(result.getModelAndView(), attribute);
            bindingResult.getGlobalErrors()
                .stream()
                .peek(globalError -> {LOGGER.info("OBJECKT NAME {} : DEFAULT MESSAGE {}", globalError.getObjectName(), globalError.getDefaultMessage());})
                .filter(globalError -> attribute.equals(globalError.getObjectName()))
                .forEach(globalError -> assertEquals("Expected default message", expectedMessage, globalError.getDefaultMessage()));
        };
    }

	
    private BindingResult getBindingResult(ModelAndView mav, String name) {
        BindingResult result = (BindingResult) mav.getModel().get(BindingResult.MODEL_KEY_PREFIX + name);
        assertTrue("No BindingResult for attribute: " + name, result != null);
        assertTrue("No global errors for attribute: " + name, result.getGlobalErrorCount() > 0);
        return result;
    }
	
	
	
}
