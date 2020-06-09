package de.ffm.rka.rkareddit.domain.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * marker for checking correct password
 */
@Documented
@Constraint(validatedBy = CorrectPasswordValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CorrectPassword {

	String message () default "Password is incorrect";
	Class<?>[] groups() default{};
	Class<? extends Payload>[] payload() default {}; 
}
