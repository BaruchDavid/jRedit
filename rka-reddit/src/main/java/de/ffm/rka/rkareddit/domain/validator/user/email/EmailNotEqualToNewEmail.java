package de.ffm.rka.rkareddit.domain.validator.user.email;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Constraint(validatedBy = EmailToNewEmailValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EmailNotEqualToNewEmail {
    String message() default "Old and new email must be different";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
