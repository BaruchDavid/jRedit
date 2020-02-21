package de.ffm.rka.rkareddit.domain.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Custom Validator for Tags
 * @author RKA
 *
 */
@Documented
@Constraint(validatedBy = TagValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TagResolver {

	String message () default "Tag is illegal";
	Class<?>[] groups() default{};
	Class<? extends Payload>[] payload() default {}; 
}
