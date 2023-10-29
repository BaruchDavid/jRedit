package de.ffm.rka.rkareddit.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Optional;

@Service
public class HttpUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);

	private HttpUtil() { }
	
	public static void listSessionAttributesFromRequest(HttpServletRequest request) {
		Enumeration<String> sessionAttributeNames = request.getSession().getAttributeNames();
		HttpSession session = request.getSession();
		while (sessionAttributeNames.hasMoreElements()) {
			Optional<String> attributeName = Optional.of(sessionAttributeNames.nextElement());
			attributeName.ifPresent(attr -> {
				LOGGER.debug("Session attribute name: {}", attr);
				Optional<Object> attribute = Optional.of(session.getAttribute(attr));
				LOGGER.debug("Session attribute value: {}", attribute.isPresent() ? attribute.get() : "no value");
			});
		}
	}

	public static void listAllRequestParams(HttpServletRequest request){
		request.getParameterMap().forEach((key, value) -> LOGGER.info("PARAM KEY {} VALUE {}", key, value));
	}

	public static String encodeParam(String value){
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}

	public static String decodeParam(String value) throws UnsupportedEncodingException {
		return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
	}

	public static HttpServletRequest getCurrentRequest() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = null;
		if (requestAttributes instanceof ServletRequestAttributes) {
			request = Optional.ofNullable(((ServletRequestAttributes) requestAttributes).getRequest())
					.orElse(null);
		}
		request = Optional.ofNullable(request)
				.orElseThrow();
		return request;
	}


}
