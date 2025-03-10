
package acme.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.internal.util.StringHelper;

import acme.realms.AirlineManager;

public class IdentifierNumberValidator implements ConstraintValidator<ValidIdentifierNumber, AirlineManager> {

	@Override
	public boolean isValid(final AirlineManager manager, final ConstraintValidatorContext context) {

		String identifier = manager.getIdentifierNumber();
		String name = manager.getIdentity().getName();
		String surname = manager.getIdentity().getSurname();

		if (StringHelper.isBlank(identifier) || StringHelper.isBlank(name) || StringHelper.isBlank(surname))
			return false;

		String initials = identifier.substring(0, 2);

		return initials.charAt(0) == Character.toUpperCase(name.charAt(0)) && initials.charAt(1) == Character.toUpperCase(surname.charAt(0));
	}
}
