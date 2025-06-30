
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.trackingLogs.TrackingLog;
import acme.entities.trackingLogs.TrackingLogRepository;
import acme.entities.trackingLogs.TrackingLogStatus;

@Validator
public class TrackingLogsValidator extends AbstractValidator<ValidTrackingLogs, TrackingLog> {

	@Autowired
	private TrackingLogRepository trackingLogsRepository;


	@Override
	protected void initialise(final ValidTrackingLogs annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final TrackingLog trackingLog, final ConstraintValidatorContext context) {
		assert context != null;

		if (trackingLog == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			Double percentage = trackingLog.getResolutionPercentage();
			TrackingLogStatus status = trackingLog.getStatus();
			String resolution = trackingLog.getResolution();

			if (percentage == null) {
				super.state(context, false, "resolutionPercentage", "The field resolution percentage can not be null");
				return false;
			}
			if (status == null) {
				super.state(context, false, "status", "The field status can not be null");
				return false;
			}

			Long countPercentage = this.trackingLogsRepository.findNumberLatestTrackingLogByClaimNotFinishExceptHimself(trackingLog.getClaim().getId(), trackingLog.getId(), percentage);

			if (countPercentage > 0)
				super.state(context, false, "resolutionPercentage", "Two trackingLogs with the same resolution percentage can not exists");

			if (!percentage.equals(100.00))
				super.state(context, status.equals(TrackingLogStatus.PENDING), "status", "Status must be PENDING");
			else
				super.state(context, !status.equals(TrackingLogStatus.PENDING), "status", "Status must be ACCEPTED or REJECTED");

			if (status.equals(TrackingLogStatus.ACCEPTED) || status.equals(TrackingLogStatus.REJECTED)) {
				boolean hasResolution = resolution != null && !resolution.isBlank();
				super.state(context, hasResolution, "resolution", "Resolution can not be empty for finished trackingLogs (REJECTED or ACCEPTED)");
			}

			if (trackingLog.getClaim().isDraftMode())
				super.state(context, trackingLog.isDraftMode(), "DrafMode", "Before publishing a trackingLog the claim must be published");

		}

		return !super.hasErrors(context);
	}

}
