package pl.clinic.project.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        String pattern1 = "\\d{9}";
        String pattern2 = "\\+48\\d{9}";

        return s.matches(pattern1) || s.matches(pattern2);
    }
}
