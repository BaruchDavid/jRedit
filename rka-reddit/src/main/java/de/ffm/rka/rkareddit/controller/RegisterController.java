package de.ffm.rka.rkareddit.controller;

import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.domain.validator.user.UserValidationGroup;
import de.ffm.rka.rkareddit.exception.GlobalControllerAdvisor;
import de.ffm.rka.rkareddit.exception.RegisterException;
import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Controller
public class RegisterController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterController.class);
    private static final String LOGGED_IN_USER = "userDto";
    private static final String REGISTRATION_REQUEST = "You have done it. Please check your email to activate your account.";
    private static final String SUCCESS = "success";
    private static final String REGISTRATION = "/registration";
    private static final String REDIRECT_MESSAGE = "redirectMessage";
    private static final String VALIDATION_ERRORS = "validationErrors";
    private static final String MAIL_ACTIVATION_FAILED = "USER %s FOR REGISTER-ACTIVATION WITH ACTIVATION-CODE %s HAS BEEN FAILED";

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    /**
     * @param model to save userDto object
     * @return view for registration
     */
    @GetMapping(value = {REGISTRATION, REGISTRATION + "/"})
    public String registration(Model model) {
        final UserDTO newUser = UserDTO.builder().build();
        UserDTO.createCaptcha(newUser);
        model.addAttribute(LOGGED_IN_USER, newUser);
        return "auth/register";
    }

    /**
     * @return user
     */
    @PostMapping(value = {REGISTRATION})
    public String userRegistration(@Validated(value = {UserValidationGroup.ValidationUserRegistration.class}) UserDTO userDto,
                                   BindingResult bindingResult, RedirectAttributes attributes, HttpServletResponse res,
                                   HttpServletRequest req, Model model) throws ServiceException, IOException {
        LOGGER.info("TRY TO REGISTER {}", userDto);
        if (bindingResult.hasErrors()) {
            UserDTO.createCaptcha(userDto);
            return manageValidationErrors(userDto, bindingResult, res, req, model);
        } else {
            userService.register(userDto);
            model.addAttribute(LOGGED_IN_USER, userDto);
            attributes.addFlashAttribute(SUCCESS, true);
            attributes.addFlashAttribute(REDIRECT_MESSAGE, REGISTRATION_REQUEST);
            LOGGER.info("REGISTER-REQUEST HAS BEEN DONE {}", userDto);
            return "redirect:".concat(REGISTRATION);
        }
    }

    /**
     * validates input for registration or email change
     *
     * @param userDto       contains failed object
     * @param bindingResult contains all errors
     * @param res           set status
     * @param req           current request
     * @param model         saves userDto model and errors
     * @return either registration view or email change view
     */
    private String manageValidationErrors(@Validated({UserValidationGroup.ValidationUserRegistration.class,
            UserValidationGroup.ValidationUserChangeEmail.class}) UserDTO userDto, BindingResult bindingResult, HttpServletResponse res, HttpServletRequest req, Model model) {
        bindingResult.getAllErrors().forEach(error -> LOGGER.warn("Register validation Error: {} during registration: {}",
                error.getCodes(), error.getDefaultMessage()));
        model.addAttribute(VALIDATION_ERRORS, bindingResult.getAllErrors());
        model.addAttribute(LOGGED_IN_USER, userDto);
        res.setStatus(HttpStatus.BAD_REQUEST.value());
        return req.getRequestURI().contains("registration") ? "auth/register" : "auth/emailChange";
    }

    @GetMapping(value = {"/activation/{email}/{activationCode}"})
    public String completeRegistration(@PathVariable String email, @PathVariable String activationCode,
                                       Model model) throws ServiceException, RegisterException {
        LOGGER.info("TRY TO ACTIVATE ACCOUNT {}", email);
        String returnLink = "auth/activated";
        Optional.ofNullable(userService.userEmailActivation(email, activationCode, false))
                .orElseThrow(() -> GlobalControllerAdvisor.createRegisterException(String.format(MAIL_ACTIVATION_FAILED,
                        email, activationCode)));
        model.addAttribute(LOGGED_IN_USER, UserDTO.builder().build());
        LOGGER.info("USER {} HAS BEEN ACTIVATED SUCCESSFULLY", email);
        return returnLink;
    }

}