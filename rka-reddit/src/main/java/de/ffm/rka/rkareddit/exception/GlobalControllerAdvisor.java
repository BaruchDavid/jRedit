package de.ffm.rka.rkareddit.exception;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.ErrorDTO;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.service.UserDetailsServiceImpl;
import de.ffm.rka.rkareddit.util.HttpUtil;
import de.ffm.rka.rkareddit.util.JsonMapper;
import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * This controller advice works only on @Controller-Classes, not
 * on @RestController-Classes
 *
 * @author kaproma
 */

@ControllerAdvice
public class GlobalControllerAdvisor {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerAdvisor.class);
    public static final String USER_ERROR_VIEW = "error/application";
    public static final String DEFAULT_APPLICATION_ERROR = "error/basicError";
    public static final String PAGE_NOT_FOUND = "error/pageNotFound";
    public static final String ANONYMOUS_USER = "anonymousUser";
    public static final String ANONYMOUS = "anonymous";

    UserDetailsServiceImpl userDetailsService;
    public GlobalControllerAdvisor(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class, UserAuthenticationLostException.class,
            NullPointerException.class,
            IllegalArgumentException.class, IllegalAccessException.class,
            NumberFormatException.class, ServiceException.class, UsernameNotFoundException.class,
            Exception.class, PreAuthenticatedCredentialsNotFoundException.class})
    public ModelAndView defaultErrorHandler(HttpServletRequest req, HttpServletResponse res, Exception exception) {
        Optional<Authentication> authentication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
        res.setHeader("location","/error");
        UserDTO user = new UserDTO();
        String view = USER_ERROR_VIEW;
        String visitorName = "";
        if (authentication.isPresent()) {
            visitorName = authentication.get().getName();
            if (!ANONYMOUS.equals(visitorName) && !ANONYMOUS_USER.equals(visitorName)) {
                user = UserDTO.mapUserToUserDto((User) userDetailsService.loadUserByUsername(visitorName));
            } else {
                user.setFirstName("guest");
            }
        } else {
            user.setFirstName("guest");
        }
        final String exceptionType = getExceptionName(exception.getClass().getCanonicalName());
       LOGGER.error("EXCEPTION TYPE {} OCCURRED: MESSAGE {} FOR USER {} ON REQUESTED URL {} : {}" +
                       " STACKTRACE: {}",
                exceptionType,
                exception.getMessage(),
                visitorName,
                req.getMethod(),
                req.getRequestURL(),
               exception);
        switch (exceptionType) {
            case "MissingServletRequestParameterException":
            case "ValidationException":
            case "MethodArgumentTypeMismatchException":
            case "IllegalArgumentException":
            case "NullPointerException":
            case "NumberFormatException":
            case "UsernameNotFoundException":
                view = DEFAULT_APPLICATION_ERROR;
                res.setStatus(HttpStatus.SC_BAD_REQUEST);
                break;
            case "UserAuthenticationLostException":
            case "AuthenticationCredentialsNotFoundException":
            case "PreAuthenticatedCredentialsNotFoundException":
                res.setStatus(HttpStatus.SC_UNAUTHORIZED);
                break;
            case "HttpRequestMethodNotSupportedException":
                view = PAGE_NOT_FOUND;
                res.setStatus(HttpStatus.SC_METHOD_NOT_ALLOWED);
                break;
            case "ServiceException":
                view = USER_ERROR_VIEW;
                res.setStatus(HttpStatus.SC_NOT_FOUND);
                break;
            default:
                res.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                view = DEFAULT_APPLICATION_ERROR;
                break;
        }

        return createErrorView(req, res, user, view, exception.getLocalizedMessage());
    }

    @ExceptionHandler(value = {MaxUploadSizeExceededException.class, IllegalVoteException.class})
    public ResponseEntity<String> defaultRestErrorHandler(HttpServletRequest req, HttpServletResponse res, Exception exception) {
        String responseBody = "Error occurred!";
        final String exceptionType = getExceptionName(exception.getClass().getCanonicalName());
        switch (exceptionType) {
            case "MaxUploadSizeExceededException":
                responseBody = "Picture file size exceeds limit!";
                break;
            case "IllegalVoteException":
                responseBody = "illegal vote!";
                break;
            default:
                break;
        }
        res.setStatus(HttpStatus.SC_BAD_REQUEST);
        LOGGER.error("ERROR ON {} WITH EXCEPTION {} AND RESPONSE-BODY {}", req.getRequestURI(), exception.getMessage(), responseBody);
        return new ResponseEntity<>(responseBody, new HttpHeaders(), org.springframework.http.HttpStatus.BAD_REQUEST);
    }

    /**
     * +JsonMapper.createJson(msg)
     * @param req to get current request of error
     * @param res from response
     * @param user
     * @param errorView for suitable error
     * @return redirect-request with json-param
     */
    private ModelAndView createErrorView(HttpServletRequest req, HttpServletResponse res, UserDTO user, String errorView, String error) {
        ModelAndView mav = new ModelAndView();
        try {
            // TODO: 04.04.2021 man muss die errorUrl anpassen, zb. für accessDenied im BasicErrorController
            // TODO: 14.03.2021 mask user-emails 
            // TODO: 14.03.2021 evaluate necesseary userContent and loggedUser
            final ErrorDTO msg = ErrorDTO.builder()
                    .loggedUser(user)
                    .userContent(user)
                    .errorView(errorView)
                    .error(error)
                    .errorStatus(res.getStatus())
                    .url(req.getRequestURL().toString())
                    .build();
            final String encodedJson = HttpUtil.encodeParam(JsonMapper.createJson(msg));
            mav.setViewName("redirect:/error?errorDTO="+encodedJson);
        } catch (Exception e) {
            LOGGER.error("FAIL TO CONVERT ERROR MESSAGE TO JSON {}", e.getMessage());
            mav.setViewName("redirect:/error");
        }
        return mav;
    }



    /**
     * determines exception name of full qualified name
     */
    private String getExceptionName(final String canonicalExcName) {
        int exceptionIndex = canonicalExcName.split("\\.").length;
        return canonicalExcName.split("\\.")[exceptionIndex - 1];
    }
}