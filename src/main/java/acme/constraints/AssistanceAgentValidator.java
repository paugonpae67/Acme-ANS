
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.AssistanceAgent;

@Validator
public class AssistanceAgentValidator extends AbstractValidator<ValidAssistanceAgent, AssistanceAgent> {

	@Override
	protected void initialise(final ValidAssistanceAgent annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final AssistanceAgent assistanceAgent, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;
		if (assistanceAgent == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			String name = assistanceAgent.getIdentity().getName();
			String surname = assistanceAgent.getIdentity().getSurname();
			if (assistanceAgent.getEmployeeCode() == null || !assistanceAgent.getEmployeeCode().matches("^[A-Z]{2-3}\\d{6}$"))
				super.state(context, false, "*", "acme.validation.employee-code.format");
			else {
				String employeeCode = assistanceAgent.getEmployeeCode();
				char employeeCodeFirstChar = Character.toUpperCase(employeeCode.charAt(0));
				char employeeCodeSecondChar = Character.toUpperCase(employeeCode.charAt(1));
				char nameFirstChar = Character.toUpperCase(name.charAt(0));
				char surnameFirstChar = Character.toUpperCase(surname.charAt(0));

				super.state(context, !(employeeCodeFirstChar == nameFirstChar), "*", "acme.validation.employee-code.initial-mismatch");
				super.state(context, !(employeeCodeSecondChar == surnameFirstChar), "*", "acme.validation.employee-code.initial-mismatch");
			}
		}
		result = !super.hasErrors(context);
		return result;
	}

}
