package fixers.jBugger.Validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AttachmentSizeValidator implements ConstraintValidator<AttachmentSizeValidation, byte[]> {

    /**
     * Checks if the length of the file is less than 25MB
     *
     * @param attachment                 File, whose length is going to be validated
     * @param constraintValidatorContext
     * @return True if the size is less than 25MB, false otherwise
     */
    @Override
    public boolean isValid(byte[] attachment, ConstraintValidatorContext constraintValidatorContext) {

        return attachment.length <= 26214400;

    }

}
