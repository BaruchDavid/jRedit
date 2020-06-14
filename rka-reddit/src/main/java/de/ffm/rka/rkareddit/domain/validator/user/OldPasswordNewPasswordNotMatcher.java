package de.ffm.rka.rkareddit.domain.validator.user;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = OldPasswordNewPasswordValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OldPasswordNewPasswordNotMatcher {

	String message () default "Password and new password must not be equal";
	Class<?>[] groups() default{};
	Class<? extends Payload>[] payload() default {};
}
