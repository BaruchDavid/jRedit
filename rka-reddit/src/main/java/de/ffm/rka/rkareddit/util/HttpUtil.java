package de.ffm.rka.rkareddit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Optional;
import java.util.stream.Stream;

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
		Stream.of(request.getParameterNames()).forEach(header -> {
			LOGGER.info("PARAM {} VALUE {}");
		});
	}

	public static String encodeParam(String value){
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}


}
