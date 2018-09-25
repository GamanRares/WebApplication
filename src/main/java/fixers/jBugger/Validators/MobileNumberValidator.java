package fixers.jBugger.Validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MobileNumberValidator implements ConstraintValidator<MobileNumberValidation, String> {

    /**
     * Checks if mobileNumber parameter is a valid phone number for RO or DE
     * <p>
     * For RO +40 or 0040 followed by 9 digits
     * For DE +49 or 0049 followed by 10 digits
     *
     * @param mobileNumber               Mobile number which is going to be validated
     * @param constraintValidatorContext
     * @return True if it is a valid phone number, false otherwise
     */
    @Override
    public boolean isValid(String mobileNumber, ConstraintValidatorContext constraintValidatorContext) {

        if (mobileNumber.matches("^((\\+40|0040)[0-9]{9}|(\\+49|0049)[0-9]{10})$"))
            return true;
        else
            return false;
    }

}
