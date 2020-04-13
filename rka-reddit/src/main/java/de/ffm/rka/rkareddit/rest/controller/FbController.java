package de.ffm.rka.rkareddit.rest.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import de.ffm.rka.rkareddit.domain.FbPost;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.service.FacebookService;

@RestController
public class FbController {
	private static final Logger LOGGER = LoggerFactory.getLogger(FbController.class);

	@Autowired
	FacebookService facebookService;

	@GetMapping("/connect/facebook")
	public ModelAndView createFacebookAuthorization(Model model) throws MalformedURLException {
		URL faceBookUrl = new URL(facebookService.createFacebookAuthorizationURL());		
		return new ModelAndView("redirect:"+faceBookUrl);
	}

	@GetMapping("/connect/facebookForm")
	public String login(HttpServletResponse response) throws IOException {
		return "/connect/facebookConnect";
	}

	
	@GetMapping("/social/facebookAccess")
	public String createFacebookAccessToken(@RequestParam("code") String code){
	    facebookService.createFacebookAccessToken(code);
	    facebookService.postLinkOnFacebook(new Link(), new FbPost());
	    return "success";
	}

	/**
	 * for testing
	 * @param fbPost contains all feed params
	 * @return post id
	 */
	@Secured({ "ROLE_USER" })
	@PostMapping("/post/fb/link/")
	public String vote(@RequestBody FbPost fbPost) {
		LOGGER.info("USER {} POSTS {}", SecurityContextHolder.getContext().getAuthentication().getName(),
				fbPost);
		try {
			facebookService.postLinkOnFacebook(new Link(), fbPost);
			
		} catch (Exception e) {
			LOGGER.error("POST ON FACEBOOK FAILED", e);
		}
		return fbPost.getFbId();
	}

}
