package fixers.jBugger.Validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<EmailValidation, String> {

    /**
     * Checks if the email parameter is a valid email address, having the following format :
     * [adresa]@msggroup.com
     *
     * @param email                      Email address as a string which is going to be validated
     * @param constraintValidatorContext
     * @return True if is a valid the email address, false otherwise
     */
    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {

        return email.matches("^[0-9a-zA-Z_]+@msggroup.com$");
    }

}
