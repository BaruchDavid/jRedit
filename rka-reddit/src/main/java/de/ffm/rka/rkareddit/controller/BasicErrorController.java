package de.ffm.rka.rkareddit.controller;


import de.ffm.rka.rkareddit.domain.dto.ErrorDTO;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
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
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Controller
public class BasicErrorController implements ErrorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicErrorController.class);
    private static final String USER_DTO = "userDto";
    public static final String ANONYMOUS = "anonymousUser";

    @Override
    public String getErrorPath() {
        return "/error";
    }


    @GetMapping(value = "/error")
    public String error(@RequestParam(value = "errorDTO", required = false) ErrorDTO errorDTO, Model model,
                        HttpServletRequest req, HttpServletResponse resp) {

        String view;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            errorDTO = Optional.ofNullable(errorDTO)
                    .orElseGet(() -> ErrorDTO.builder()
                            .errorStatus(resp.getStatus())
                            .error(req.getRequestURI())
                            .build());
            view = errorDTO.getErrorView().isEmpty() ? "error/basicError" : errorDTO.getErrorView();
            UserDTO userDto = !ANONYMOUS.equals(authentication.getName()) ?
                    Optional.ofNullable(errorDTO.getLoggedUser())
                            .orElseGet(UserService::buildAnonymousUser) : UserService.buildAnonymousUser();
            LOGGER.error("EXCEPTION {} ON REQUEST {} WITH STATUS {}", errorDTO.getError(), errorDTO.getUrl(), errorDTO.getErrorStatus());
            model.addAttribute(USER_DTO, userDto);
            resp.setStatus(errorDTO.getErrorStatus());
        } catch (Exception e) {
            LOGGER.error("EXCEPTION ON PROCESSING ERROR-HANDLER {} with orig exception {}", e.getMessage(), errorDTO.getError());
            model.addAttribute(USER_DTO, UserService.buildAnonymousUser());
            view = "error/pageNotFound";
        }
        return view;
    }


    @GetMapping("/error/registrationError")
    public String registrationError(HttpServletRequest request, HttpServletResponse resp, Exception ex) {
        LOGGER.error("SHOW REGISTRATION-ERROR-VEW {} WITH EXCEPTION {}", request.getRequestURI(), ex.getMessage());
        resp.setStatus(HttpStatus.SC_BAD_REQUEST);
        return "error/registrationError";
    }

    /**
     * will be accessed from GlobalAccessDeniedHandler
     *
     * @param request
     * @param resp
     * @param ex
     * @param model
     * @return
     */
    @GetMapping("/error/accessDenied")
    public String accessDenied(HttpServletRequest request, HttpServletResponse resp, Exception ex, Model model) {
        LOGGER.error("SHOW ACCESS-DENIED-VEW {} WITH EXCEPTION {}", request.getRequestURI(), ex.getMessage());
        model.addAttribute(USER_DTO, UserDTO.builder()
                .firstName(request.getParameter("name"))
                .build());
        resp.setStatus(403);
        return "error/accessDenied";
    }
}
