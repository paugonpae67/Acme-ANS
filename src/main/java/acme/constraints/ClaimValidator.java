
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.claim.Claim;
import acme.entities.legs.Leg;

@Validator
public class ClaimValidator extends AbstractValidator<ValidClaims, Claim> {

	@Override
	protected void initialise(final ValidClaims annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Claim claim, final ConstraintValidatorContext context) {
		assert context != null;

		Leg leg = claim.getLeg();

		if (leg == null) {
			super.state(context, false, "Leg", "Leg can not be null");
			return false;
		}

		if (claim.getLeg().isDraftMode())
			super.state(context, false, "Leg", "The Leg must be published before it can be linked to the claim");

		if (claim.getLeg().getScheduledArrival().compareTo(claim.getRegistrationMoment()) > 0)
			super.state(context, false, "Leg", "The Leg must have happened before the claim");

		if (claim.getLeg().getAircraft().getAirline().getId() != claim.getAssistanceAgent().getAirline().getId())
			super.state(context, false, "Leg", "The assistance agent must belong to the airline operating the flight");

		return !super.hasErrors(context);
	}

}
