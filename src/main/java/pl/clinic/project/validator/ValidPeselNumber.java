package pl.clinic.project.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;


import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = PeselNumberValidator.class)
public @interface ValidPeselNumber {
    String message() default "Incorrect pesel number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
