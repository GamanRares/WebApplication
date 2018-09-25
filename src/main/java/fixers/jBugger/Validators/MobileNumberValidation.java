package fixers.jBugger.Validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = MobileNumberValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface MobileNumberValidation {

    String message() default "Mobile Number is incorrect";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
