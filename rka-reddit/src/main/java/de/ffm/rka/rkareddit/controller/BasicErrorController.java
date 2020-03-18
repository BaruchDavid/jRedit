package de.ffm.rka.rkareddit.controller;

import java.util.Optional;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;

@Controller
public class BasicErrorController implements ErrorController{
	private static final Logger LOGGER = LoggerFactory.getLogger(BasicErrorController.class);
	public static final String ANONYMOUS = "anonymousUser";
	@Autowired
	UserDetailsServiceImpl userDetailsService;
	
	@Override
	public String getErrorPath() {
		return "/error";
	}
	
	@RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
		Authentication authetication = SecurityContextHolder.getContext().getAuthentication();
		User user = User.builder()
						.firstName("Gast")
						.secondName("")
						.build();
		if(!ANONYMOUS.equals(authetication.getName())){
			user = (User) userDetailsService.loadUserByUsername(authetication.getName());
		}
		model.addAttribute("user", user);
        return "error/pageNotFound";
    }

}
