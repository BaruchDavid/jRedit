package de.ffm.rka.rkareddit.controller;


import de.ffm.rka.rkareddit.domain.dto.ErrorDTO;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
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
import java.util.function.Supplier;

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
    public String error(@RequestParam(value = "errorDTO", required = false) ErrorDTO errorDTO, Model model, HttpServletResponse resp) {

        String view;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            errorDTO = Optional.ofNullable(errorDTO)
                                .orElseGet(ErrorDTO::new);
            view = errorDTO.getErrorView().isEmpty() ? "error/basicError": errorDTO.getErrorView();
            UserDTO userDto = !ANONYMOUS.equals(authentication.getName()) ?
                    Optional.ofNullable(errorDTO.getLoggedUser())
                            .orElseGet(this::buildAnonymousUser) : buildAnonymousUser();
            LOGGER.error("EXCEPTION {} ON REQUEST {} WITH STATUS {}", errorDTO.getError(), errorDTO.getUrl(), errorDTO.getErrorStatus());
            model.addAttribute(USER_DTO, userDto);
            resp.setStatus(errorDTO.getErrorStatus());
        } catch (Exception e) {
            LOGGER.error("EXCEPTION ON PROCESSING ERROR-HANDLER {} with orig exception {}", e.getMessage(), errorDTO.getError());
            model.addAttribute(USER_DTO, buildAnonymousUser());
            view = "error/pageNotFound";
        }
        return view;
    }

    private UserDTO buildAnonymousUser() {
        Supplier<UserDTO> userDTOSupplier = () -> UserDTO.builder()
                .firstName("Guest")
                .secondName("")
                .build();
        return userDTOSupplier.get();
    }

    @GetMapping("/error/registrationError")
    public String registrationError(HttpServletRequest request, HttpServletResponse resp, Exception ex) {
        LOGGER.error("SHOW REGISTRATION-ERROR-VEW {} WITH EXCEPTION {}", request.getRequestURI(), ex.getMessage());
        resp.setStatus(HttpStatus.SC_BAD_REQUEST);
        return "error/registrationError";
    }

    // TODO: 28.02.2021 getUserDTO(authentication.getName() ersetzen mit errorDto aus dem GlobalAdvisor
    @GetMapping("/error/accessDenied")
    public String accessDenied(HttpServletRequest request, HttpServletResponse resp, Exception ex, Model model) {
        LOGGER.error("SHOW ACCESS-DENIED-VEW {} WITH EXCEPTION {}", request.getRequestURI(), ex.getMessage());
        model.addAttribute(USER_DTO, UserDTO.builder().build());
        resp.setStatus(403);
        return "error/accessDenied";
    }
}
