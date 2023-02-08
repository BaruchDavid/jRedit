package de.ffm.rka.rkareddit.domain.validator.user.password;

import de.ffm.rka.rkareddit.domain.dto.UserDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * validates given constraint for given object
 *
 * @author RKA
 */
public class PasswordMatchValidator implements ConstraintValidator<PasswordMatcher, UserDTO> {

    /**
     * checks new password is matching of confirmed new password during registration
     */
    @Override
    public boolean isValid(UserDTO user, ConstraintValidatorContext context) {

        return user.getPassword().equals(user.getConfirmPassword());
    }
}
