package de.ffm.rka.rkareddit.domain.validator.user.email;

import de.ffm.rka.rkareddit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<IsEmailUnique, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String userEmail, ConstraintValidatorContext constraintValidatorContext) {
        return userRepository.findByEmail(userEmail).isEmpty();
    }
}
