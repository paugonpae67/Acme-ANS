
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.trackingLogs.TrackingLog;
import acme.entities.trackingLogs.TrackingLogStatus;

@Validator
public class TrackingLogsValidator extends AbstractValidator<ValidTrackingLogs, TrackingLog> {

	@Override
	protected void initialise(final ValidTrackingLogs annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final TrackingLog trackingLog, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result = true;

		if (trackingLog == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			result = !super.hasErrors(context);
		} else {
			if (trackingLog.getStatus().equals(TrackingLogStatus.PENDING) && trackingLog.getResolutionPercentage() == 100.00)
				super.state(context, false, "Status", "The status can be “PENDING” only when the resolution percentage is not 100%");
			else if (!trackingLog.getStatus().equals(TrackingLogStatus.PENDING) && trackingLog.getResolutionPercentage() != 100.00)
				super.state(context, false, "Status", "The status can be “ACCEPTED” or “REJECTED” only when the resolution percentage gets to 100%");
			else if (!trackingLog.getStatus().equals(TrackingLogStatus.PENDING) && (trackingLog.getResolution() == null || trackingLog.getResolution().isBlank()))
				super.state(context, false, "Status", "If the status is not “PENDING”, then the resolution is mandatory");
			//else if (trackingLog.getClaim().isDraftMode())
			//super.state(context, false, "Claim", "We cannot associate a tracking log with a claim in draft mode.");
			result = !super.hasErrors(context);
		}
		return result;
	}
}
