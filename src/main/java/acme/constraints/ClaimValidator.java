
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.claim.Claim;
import acme.entities.trackingLogs.TrackingLogRepository;

@Validator
public class ClaimValidator extends AbstractValidator<ValidClaims, Claim> {

	@Autowired
	private TrackingLogRepository trackingLogsRepository;


	@Override
	protected void initialise(final ValidClaims annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Claim claim, final ConstraintValidatorContext context) {
		assert context != null;

		if (claim.getLeg().isDraftMode())
			super.state(context, false, "Leg", "The Leg must be published before it can be linked to the claim");

		return !super.hasErrors(context);
	}

}
