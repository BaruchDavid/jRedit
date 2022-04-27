package de.ffm.rka.rkareddit.controller;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.domain.validator.user.UserValidationgroup;
import de.ffm.rka.rkareddit.domain.validator.user.UserValidationgroup.UnauthenticatedUserRecoverPassword;
import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Controller
public class PwRecoverController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PwRecoverController.class);
    private static final String LOGGED_IN_USER = "userDto";
    private static final String CONTENT_USER = "userContent";
    private static final String SUCCESS = "success";
    private static final String REDIRECT_MESSAGE = "redirectMessage";
    private static final String BINDING_ERROR = "bindingError";
    private static final String VALIDATION_ERRORS = "validationErrors";
    private static final String ERROR_MESSAGE = "Update user validation Error: {} message: {}";
    private static final String REDIRECT_TO_PRIVATE_PROFILE = "redirect:/profile/private";
    private final UserService userService;
    private static final String NOT_LOGGED_IN = "notLoggedIn";


    public PwRecoverController(UserService userService) {
        this.userService = userService;

    }

    @GetMapping("/profile/user/recover/view")
    public String showRecoverUserPwRequest(Model model) {
        LOGGER.info("SHOW VIEW FOR PASSWORD RECOVERING");
        model.addAttribute(LOGGED_IN_USER, UserDTO.builder().email(NOT_LOGGED_IN).build());
        model.addAttribute(CONTENT_USER, UserDTO.builder().firstName("Guest").build());
        return "recover/recoverUserPwRequest";
    }

    @GetMapping("/profile/user/recover/{email}/{activationCode}")
    public String getPasswordRecoveryForm(@PathVariable String email, @PathVariable String activationCode, RedirectAttributes attributes, Model model)
            throws ServiceException {
        LOGGER.info("TRY TO SHOW PW-RECOVER-VIEW FOR USER {} ON GIVEN ACTIVATION-CODE {}", email, activationCode);
        String returnLink = "recover/passwordRecoveryForm";
        Optional<UserDTO> userDTO = userService.getUserForPasswordReset(email, activationCode);
        final boolean isPwRecoveringExpired = userDTO
                .map(userForRecovering -> userService.isActivationDeadlineExpired(userForRecovering.getActivationDeadLineDate()))
                .orElse(false);

        if (isPwRecoveringExpired) {
            attributes.addFlashAttribute(SUCCESS, false);
            attributes.addFlashAttribute(REDIRECT_MESSAGE, "Link for password recovering is expired! \nNavigate to Home page");
            LOGGER.error("USER {} PASSED ACTIVATION-CODE {} FOR PW-RECOVERING NOT SUCCESSFULLY", email, activationCode);
            return "redirect:/error/pwRecover";
        } else {
            model.addAttribute(LOGGED_IN_USER, UserDTO.builder().email(NOT_LOGGED_IN).build());
            model.addAttribute(CONTENT_USER, UserDTO.builder().email(email).build());
            LOGGER.error("USER {} PASSED ACTIVATION-CODE {} FOR PW-RECOVERING SUCCESSFULLY", email, activationCode);
            return returnLink;
        }
    }

    @GetMapping("/error/pwRecover")
    public String showRecoveringError(Model model) {
        model.addAttribute(REDIRECT_MESSAGE, model.asMap().get(REDIRECT_MESSAGE));
        return "/error/pwRecoverError";
    }

    /**
     * when user forget his credentials for login
     * @param userEmail for recovering
     * @param model to saving data for view
     * @return view
     * @throws ServiceException if new activation code could not be saved
     */
    @PostMapping("/profile/user/recover/")
    public String createActivationCodeAndSendMail(@RequestParam String userEmail, Model model) throws ServiceException {
        LOGGER.info("TRY TO CREATE ACTIVATION CODE AND SEND MAIL WITH EMAIL FOR PW-RECOVERING {}", userEmail);
        final Optional<User> user = userService.findUserById(userEmail);
        if (user.isPresent()){
            userService.saveNewActionCode(user.get());
        }
        model.addAttribute(LOGGED_IN_USER, UserDTO.builder().email(NOT_LOGGED_IN).build());
        model.addAttribute(CONTENT_USER, UserDTO.builder().firstName("Guest").build());
        model.addAttribute(SUCCESS, true);
        LOGGER.info("ACTIVATION CODE AND SEND MAIL WITH EMAIL FOR PW-RECOVERING SUCCESSFUL {}", userEmail);
        return "recover/recoverUserPwRequestApplied";

    }

    @PutMapping("/profile/user/recover")
    public String userPasswordRecovery(@Validated(UnauthenticatedUserRecoverPassword.class) UserDTO userDto,
                                       BindingResult bindingResult, HttpServletResponse res, RedirectAttributes attributes, Model model) throws ServiceException {
        LOGGER.info("TRY TO SAVE NEW PASSWORD FOR PW-RECOVERING FOR USER{}", userDto.getEmail());
        if (bindingResult.hasErrors()) {
            manageValidationErrors(userDto, bindingResult, res, attributes, model);
            return "auth/passwordChange";
        } else {
            userService.changeUserPassword(userDto);
            attributes.addFlashAttribute(SUCCESS, true);
            attributes.addFlashAttribute(REDIRECT_MESSAGE, "your password has been changed!");
            res.setStatus(HttpStatus.PERMANENT_REDIRECT.value());
            LOGGER.info("USER PASSWORD RECOVERED SUCCESSFULLY {}", userDto.getEmail());
            return REDIRECT_TO_PRIVATE_PROFILE;
        }
    }

    private void manageValidationErrors(@Validated(UserValidationgroup.ValidationChangeUserProperties.class) UserDTO userDto,
                                        BindingResult bindingResult, HttpServletResponse res,
                                        RedirectAttributes attributes, Model model) {
        bindingResult.getAllErrors().forEach(error -> LOGGER.warn(ERROR_MESSAGE,
                error.getCodes(), error.getDefaultMessage()));
        model.addAttribute(VALIDATION_ERRORS, bindingResult.getAllErrors());
        model.addAttribute(LOGGED_IN_USER, userDto);
        attributes.addFlashAttribute(BINDING_ERROR, true);
        res.setStatus(HttpStatus.BAD_REQUEST.value());
    }
}