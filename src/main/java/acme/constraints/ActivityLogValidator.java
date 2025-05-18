
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.legs.Leg;

@Validator
public class ActivityLogValidator extends AbstractValidator<ValidActivityLog, ActivityLog> {

	@Override
	protected void initialise(final ValidActivityLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final ActivityLog activity, final ConstraintValidatorContext context) {

		assert context != null;
		boolean result;

		FlightAssignment assignment = activity.getFlightAssignment();
		Leg leg = activity.getFlightAssignment().getLeg();

		if (activity == null)
			super.state(context, false, "nextInspection", "acme.validation.activityLog.NotNull");
		else if (activity.getFlightAssignment() == null || activity.getRegistrationMoment() == null || leg == null)
			super.state(context, false, "nextInspection", "acme.validation.activityLog.nextInspectionNotNull");
		else {
			boolean correctLeg;
			correctLeg = MomentHelper.isBefore(leg.getScheduledArrival(), activity.getRegistrationMoment()) && !leg.isDraftMode();

			super.state(context, correctLeg, "nextInspection", "acme.validation.activityLog.legCorrect");

		}
		result = !super.hasErrors(context);

		return result;

	}

}
