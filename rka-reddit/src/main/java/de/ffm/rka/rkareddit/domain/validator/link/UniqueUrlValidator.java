package de.ffm.rka.rkareddit.domain.validator.link;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueUrlValidator implements ConstraintValidator<UniqueUrl, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
