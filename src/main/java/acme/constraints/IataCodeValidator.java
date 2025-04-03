
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;

@Validator
public class IataCodeValidator extends AbstractValidator<ValidIataCode, String> {

	@Override
	protected void initialise(final ValidIataCode annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final String iataCode, final ConstraintValidatorContext context) {

		assert context != null;

		if (iataCode == null || iataCode.matches("^[A-Z]{3}$") || iataCode.length() == 3)
			return true;
		else
			return false;

	}
}
