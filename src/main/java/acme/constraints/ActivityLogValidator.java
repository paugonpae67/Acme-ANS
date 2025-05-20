
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
		if (activity.getFlightAssignment() == null || activity.getRegistrationMoment() == null)
			super.state(context, false, "flightAssignment", "acme.validation.activityLog.nextInspectionNotNull");
		if (activity.getFlightAssignment() != null && (leg == null || leg.isDraftMode()))
			super.state(context, false, "flightAssignment", "acme.validation.activityLog.NotValidLeg");
		if (!MomentHelper.isBefore(leg.getScheduledArrival(), activity.getRegistrationMoment()))
			super.state(context, false, "flightAssignment", "acme.validation.activityLog.legIncorrect");

		result = !super.hasErrors(context);

		return result;

	}

}
