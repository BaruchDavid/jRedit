package de.ffm.rka.rkareddit.domain.validator.captcha;

import de.ffm.rka.rkareddit.domain.dto.UserDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CaptchaValidator implements ConstraintValidator<CaptchaCheck, UserDTO> {
    @Override
    public boolean isValid(UserDTO userDTO, ConstraintValidatorContext constraintValidatorContext) {
        return userDTO.getHiddenCaptcha().equals(userDTO.getCaptcha());
    }
}
