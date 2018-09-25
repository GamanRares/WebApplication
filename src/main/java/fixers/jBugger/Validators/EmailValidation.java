package fixers.jBugger.Validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = EmailValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface EmailValidation {

    String message() default "Email is incorrect";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
