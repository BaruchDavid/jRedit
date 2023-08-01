package de.ffm.rka.rkareddit.interceptor;

import de.ffm.rka.rkareddit.exception.UserAuthenticationLostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * purpose of this class is checking of authenticated users by using
 * annotation @AuthenticationPrincipal
 *
 * @author rka
 */
public class ApplicationHandlerInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationHandlerInterceptor.class);
    public static final String PRIVATE_PROFILE_URL = "private";

    /**
     * any method with @AuthenticationPrincipal and without @Secured
     *
     * @throws Exception in pre-handle of request
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean isForwardRequestToTargetURL = true;
        MDC.put("session_id", request.getSession().getId());
        MDC.put("user_ip", request.getRemoteAddr());


        LOGGER.info("ACCESS IN PRE-HANDLE-INTERCEPTOR WITH URL: {} "
                        + "  {} WITH STATUS: {} FROM REMOTE ADDRESS {}",
                request.getMethod(), request.getRequestURL(), response.getStatus(), request.getRemoteAddr());


        if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();
            if (method.getParameters()[0].getAnnotation(AuthenticationPrincipal.class) instanceof AuthenticationPrincipal
                    && PRIVATE_PROFILE_URL.contains(request.getRequestURL())) {
                LOGGER.info("METHODE: {}", method.getName());
                LOGGER.warn("authenticated user could not access method with authentication");
                LOGGER.warn("Browser-Info {}", request.getHeader("user-agent"));
                LOGGER.warn("IP-Address {}", request.getHeader("True-Client-IP"));
                LOGGER.warn("Remote Address {}", request.getRemoteAddr());
                throw new UserAuthenticationLostException("LOST AUTHENTICATION-CONTEXT");
            }
        }
        return isForwardRequestToTargetURL;
    }

    public static List<String> getRequestHeaderList(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        List<String> resultList = new ArrayList<>();

        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = "";
                Enumeration<String> header = request.getHeaders(headerName);
                while (header != null && header.hasMoreElements()) {
                    headerValue = headerValue.concat(",").concat(header.nextElement());
                }
                if (headerValue.length() > 0) {
                    headerValue = headerValue.substring(1);
                }
                resultList.add(headerName.concat("=").concat(headerValue));
            }
        }
        return resultList;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {

        if (isClientError(response.getStatus()) && notFromErrorHandler(request.getRequestURL().toString())) {
            LOGGER.info("ACCESS IN POST-HANDLE-INTERCEPTOR CLIENT ERROR: WITH URL: {} "
                            + " {} WITH STATUS: {} FROM REMOTE ADDRESS {}",
                    request.getMethod(), request.getRequestURL(), response.getStatus(), request.getRemoteAddr());
        } else if (!notFromErrorHandler(request.getRequestURL().toString())) {
            LOGGER.info("ACCESS IN POST-HANDLE-INTERCEPTOR FROM ERROR-URL WITH ERROR-STATUS {} OF PREVIOUS ERROR",
                    response.getStatus());
        }
        MDC.clear();
    }

    private boolean isClientError(int httpStatus) {
        return httpStatus == HttpStatus.BAD_REQUEST.value() || httpStatus == HttpStatus.NOT_FOUND.value();
    }

    private boolean notFromErrorHandler(String url) {
        return !url.contains("/error");
    }
}
