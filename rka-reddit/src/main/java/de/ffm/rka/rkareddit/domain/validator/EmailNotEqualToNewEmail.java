package de.ffm.rka.rkareddit.domain.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;


@Constraint(validatedBy = EmailValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EmailNotEqualToNewEmail {
	String message () default "old and new email must be different";
	Class<?>[] groups() default{};
	Class<? extends Payload>[] payload() default {}; 
}
