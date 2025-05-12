
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.flightAssignment.FlightAssignment;

@Validator
public class FlightAssignmetnValidator extends AbstractValidator<ValidFlightAssignment, FlightAssignment> {

	@Override
	protected void initialise(final ValidFlightAssignment annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final FlightAssignment assignment, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		if (MomentHelper.isPast(assignment.getLeg().getScheduledArrival()))
			return true;
		else
			return false;
	}

}
