
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;

@Validator
public class LicenseNumberValidator extends AbstractValidator<ValidLicenseNumber, String> {

	@Override
	protected void initialise(final ValidLicenseNumber annotation) {
		assert annotation != null;
	}
	@Override
	public boolean isValid(final String licenseNumber, final ConstraintValidatorContext context) {

		assert context != null;

		if (licenseNumber == null || licenseNumber.isBlank())
			return true;
		if (licenseNumber.matches("^[A-Z]{2,3}\\d{6}$"))
			return true;
		else
			return false;

	}
}
