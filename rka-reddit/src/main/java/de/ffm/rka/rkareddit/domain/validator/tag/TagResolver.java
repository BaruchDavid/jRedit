package de.ffm.rka.rkareddit.domain.validator.tag;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom Validator for Tags
 *
 * @author RKA
 */
@Documented
@Constraint(validatedBy = TagValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TagResolver {

    String message() default "Tag is illegal";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
