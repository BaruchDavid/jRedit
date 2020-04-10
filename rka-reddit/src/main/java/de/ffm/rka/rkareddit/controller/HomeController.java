package de.ffm.rka.rkareddit.controller;


import java.util.Enumeration;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/")
public class HomeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

	public HomeController() {
	}

	@Deprecated(since = "in the moment not used")
	@GetMapping({"/",""})
	public String list(HttpServletRequest request, 
						@PageableDefault(size = 6, direction = Sort.Direction.DESC, sort = "linkId") Pageable page,
						@AuthenticationPrincipal UserDetails user, Model model) {
		Enumeration<String> sessionAttributeNames = request.getSession().getAttributeNames();
		HttpSession session = request.getSession();
		while(sessionAttributeNames.hasMoreElements()) {
			Optional<String> attributeName = Optional.of(sessionAttributeNames.nextElement());
			attributeName.ifPresent(attr -> {
				LOGGER.debug("Session attribute name: {}", attr);
				Optional<Object> attribute = Optional.of(session.getAttribute(attr));
				LOGGER.debug("Session attribute value: {}", attribute.isPresent()?attribute.get():"no value");
			});
		}
		return "link/link_list";
		 
	}
}
