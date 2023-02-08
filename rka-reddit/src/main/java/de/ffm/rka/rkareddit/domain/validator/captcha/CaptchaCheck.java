package de.ffm.rka.rkareddit.domain.validator.captcha;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {CaptchaValidator.class})
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CaptchaCheck {

    String message() default "please enter captcha like on picture";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
