package fixers.jBugger.Validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = AttachmentSizeValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface AttachmentSizeValidation {

    String message() default "Attachment size has to be maximum 25 MB";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
