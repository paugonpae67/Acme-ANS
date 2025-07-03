
package acme.constraints;

import java.lang.reflect.Method;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.basis.AbstractRealm;
import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;

@Validator
public class UserIdentifierValidator extends AbstractValidator<ValidUserIdentifier, AbstractRealm> {

	private static final String[] IDENTIFIER_METHODS = {
		"getIdentifier", "getEmployeeCode", "getIdentifierNumber"
	};


	@Override
	public void initialise(final ValidUserIdentifier annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final AbstractRealm user, final ConstraintValidatorContext context) {
		assert context != null;
		boolean result;

		if (user == null)
			super.state(context, true, "*", "javax.validation.constraints.NotNull.message");
		else {
			String name = user.getIdentity().getName();
			String surname = user.getIdentity().getSurname();
			String identifier = this.getIdentifier(user);
			if (identifier == null || !identifier.matches("^[A-Z]{2,3}\\d{6}$"))
				super.state(context, true, "employeeCode", "acme.validation.identifier.format");
			else {
				char identifierFirstChar = Character.toUpperCase(identifier.charAt(0));
				char identifierSecondChar = Character.toUpperCase(identifier.charAt(1));
				char nameFirstChar = Character.toUpperCase(name.charAt(0));
				char surnameFirstChar = Character.toUpperCase(surname.charAt(0));

				super.state(context, identifierFirstChar == nameFirstChar, "employeeCode", "acme.validation.identifier.initial-mismatch");
				super.state(context, identifierSecondChar == surnameFirstChar, "employeeCode", "acme.validation.identifier.initial-mismatch");
			}
		}
		result = !super.hasErrors(context);
		return result;
	}

	/**
	 * Intenta obtener el identificador llamando a uno de los métodos conocidos.
	 */
	private String getIdentifier(final AbstractRealm user) {
		for (String methodName : UserIdentifierValidator.IDENTIFIER_METHODS)
			try {
				Method method = user.getClass().getMethod(methodName);
				Object result = method.invoke(user);
				if (result instanceof String)
					return (String) result;
			} catch (NoSuchMethodException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {

			}
		return null;
	}
}
