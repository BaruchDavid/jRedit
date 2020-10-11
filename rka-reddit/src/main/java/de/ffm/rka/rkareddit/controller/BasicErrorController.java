package de.ffm.rka.rkareddit.controller;


import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;
import de.ffm.rka.rkareddit.service.UserService;

import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private final UserDetailsServiceImpl userDetailsService;
	
	@Override
	public String getErrorPath() {
		return "/error";
	}

	public BasicErrorController(UserDetailsServiceImpl userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@GetMapping("/error")
    public String error(HttpServletRequest request, HttpServletResponse resp, Exception ex, Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDTO userDto = UserDTO.builder()
						.firstName("Guest")
						.secondName("")
						.build();
		if(!ANONYMOUS.equals(authentication.getName())){			
			userDto =  getUserDTO(authentication.getName());
		}
		LOGGER.error("EXCEPTION {} REQUEST {} STATUS {}", request.getRequestURL(), ex.getMessage(), resp.getStatus());
		model.addAttribute("userDto", userDto);
		resp.setStatus(404);
        return "error/pageNotFound";
    }
	
	@GetMapping("/error/registrationError")
    public String registrationError(HttpServletRequest request, HttpServletResponse resp, Exception ex) {
		LOGGER.error("SHOW REGISTRATION-ERROR-VEW {} WITH EXCEPTION {}", request.getRequestURI(), ex.getMessage());
		resp.setStatus(HttpStatus.SC_BAD_REQUEST);
        return "error/registrationError";
    }

	@GetMapping("/error/accessDenied")
    public String accessDenied(HttpServletRequest request, HttpServletResponse resp, Exception ex, Model model) {
		LOGGER.error("SHOW ACCESS-DENIED-VEW {} WITH EXCEPTION {}", request.getRequestURI(), ex.getMessage());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		model.addAttribute("userDto", getUserDTO(authentication.getName()));
		resp.setStatus(403);
		return "error/accessDenied";
    }
	
	/**TODO: vermeide user aus der DB abzufragen, hole Vornamen, Nachnamen aus der Authetication*/
	private UserDTO getUserDTO(String userName) {
		User user = (User) userDetailsService.loadUserByUsername(userName);
		return  UserDTO.mapUserToUserDto(user);
	}
}
