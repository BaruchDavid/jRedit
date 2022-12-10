package de.ffm.rka.rkareddit.domain.validator.link;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueUrlValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUrl {


    String message() default "Link must be unique";

    /**
     * contains marker-infaces, which will be used for this annotation
     */
    Class<?>[] groups() default {};

    /**
     * payload is a parameter, a value, which will be validated by Validator
     */
    Class<? extends Payload>[] payload() default {};

}
