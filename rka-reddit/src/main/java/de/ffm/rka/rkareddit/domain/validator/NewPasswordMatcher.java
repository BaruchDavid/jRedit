package de.ffm.rka.rkareddit.domain.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = NewPasswordValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NewPasswordMatcher {

	String message () default "New password and password confirmation must be equal";
	Class<?>[] groups() default{};
	Class<? extends Payload>[] payload() default {}; 
}
