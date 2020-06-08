package de.ffm.rka.rkareddit.controller;


import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	
	@GetMapping("/error")
    public String error(HttpServletRequest request, HttpServletResponse resp, Exception ex, Model model) {
		Authentication authetication = SecurityContextHolder.getContext().getAuthentication();
		UserDTO user = UserDTO.builder()
						.firstName("Guest")
						.secondName("")
						.build();
		if(!ANONYMOUS.equals(authetication.getName())){			
			user =  userDetailsService.mapUserToUserDto(authetication.getName());
		}
		LOGGER.error("EXCEPTION {} REQUEST {} STATUS {}", request.getRequestURL(), ex.getMessage(), resp.getStatus());
		model.addAttribute("userDto", user);
		resp.setStatus(404);
        return "error/pageNotFound";
    }
	
	@GetMapping("/error/registrationError")
    public String registrationError(HttpServletRequest request, HttpServletResponse resp, Exception ex, Model model) {
		LOGGER.error("SHOW REGISTRATION-ERROR-VEW");
        return "error/registrationError";
    }

	@GetMapping("/error/accessDenied")
    public String accessDenied(HttpServletRequest request, HttpServletResponse resp, Exception ex, Model model) {
		Authentication authetication = SecurityContextHolder.getContext().getAuthentication();
		UserDTO user =  userDetailsService.mapUserToUserDto(authetication.getName());
		model.addAttribute("userDto", user);
		resp.setStatus(403);
		return "error/accessDenied";
    }
}
