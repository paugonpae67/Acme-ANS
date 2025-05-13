
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;

@Validator
public class FlightAssignmetnValidator extends AbstractValidator<ValidFlightAssignment, ActivityLog> {

	@Override
	protected void initialise(final ValidFlightAssignment annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final ActivityLog activity, final ConstraintValidatorContext context) {

		assert context != null;

		FlightAssignment assignment = activity.getFlightAssignment();
		if (MomentHelper.isPast(assignment.getLeg().getScheduledArrival()) && MomentHelper.isPast(assignment.getLeg().getScheduledDeparture()))
			return true;
		else
			return false;
	}

}
