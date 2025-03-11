
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.Customer;

@Validator
public class CustomerValidator extends AbstractValidator<ValidCustomer, Customer> {

	@Override
	protected void initialise(final ValidCustomer annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Customer customer, final ConstraintValidatorContext context) {
		assert context != null;
		boolean result;

		if (customer == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			String name = customer.getIdentity().getName();
			String surname = customer.getIdentity().getSurname();
			if (customer.getIdentifier() == null || !customer.getIdentifier().matches("^[A-Z]{2,3}\\d{6}$"))
				super.state(context, false, "*", "acme.validation.identifier.format");
			else {
				String identifier = customer.getIdentifier();
				char identifierFirstChar = Character.toUpperCase(identifier.charAt(0));
				char identifierSecondChar = Character.toUpperCase(identifier.charAt(1));
				char nameFirstChar = Character.toUpperCase(name.charAt(0));
				char surnameFirstChar = Character.toUpperCase(surname.charAt(0));

				super.state(context, !(identifierFirstChar == nameFirstChar), "*", "acme.validation.identifier.initial-mismatch");
				super.state(context, !(identifierSecondChar == surnameFirstChar), "*", "acme.validation.identifier.initial-mismatch");
			}
		}
		result = !super.hasErrors(context);
		return result;
	}

}
