
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;

@Validator
public class PhoneValidator extends AbstractValidator<ValidPhone, String> {

	@Override
	protected void initialise(final ValidPhone annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final String phoneNumber, final ConstraintValidatorContext context) {

		assert context != null;

		if (phoneNumber == null || phoneNumber.isBlank() || phoneNumber.matches("^\\+?\\d{6,15}$"))
			return true;
		else
			return false;

	}
}
