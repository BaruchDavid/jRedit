package de.ffm.rka.rkareddit.domain.validator.user.password;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom Validator for Password
 *
 * @author RKA
 */
@Documented
@Constraint(validatedBy = PasswordMatchValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatcher {

    String message() default "Password and password confirmation do not match";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
