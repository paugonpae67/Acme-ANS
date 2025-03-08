
package acme.constraints;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;

@Validator
public class NextInspectionValidator extends AbstractValidator<ValidNextInspectionDue, Date> {

	@Override
	protected void initialise(final ValidNextInspectionDue annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Date nextInspectionDue, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;
		if (nextInspectionDue == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			Date minimumNextInspectionDue;
			boolean correctNextInspectionDue;
			minimumNextInspectionDue = MomentHelper.deltaFromCurrentMoment(1, ChronoUnit.HOURS);
			correctNextInspectionDue = MomentHelper.isAfterOrEqual(nextInspectionDue, minimumNextInspectionDue);

			super.state(context, correctNextInspectionDue, "*", "acme.validation.job.deadline.message");
		}
		result = !super.hasErrors(context);

		return result;
	}

}
