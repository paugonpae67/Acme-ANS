
package acme.constraints;

import java.util.List;

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
				super.state(context, false, "ResolutionPercentage", "The field resolution percentage can not be null");
				return false;
			}
			if (status == null) {
				super.state(context, false, "Status", "The field status can not be null");
				return false;
			}

			Long countPercentage = this.trackingLogsRepository.findNumberLatestTrackingLogByClaimNotFinishExceptHimself(trackingLog.getClaim().getId(), trackingLog.getId(), percentage);

			if (countPercentage > 0)
				super.state(context, false, "ResolutionPercentage", "Two trackingLogs with the same resolution percentage can not exists");

			if (!percentage.equals(100.00))
				super.state(context, status.equals(TrackingLogStatus.PENDING), "Status", "Status must be PENDING");
			else
				super.state(context, !status.equals(TrackingLogStatus.PENDING), "Status", "Status must be ACCEPTED or REJECTED");

			if (status.equals(TrackingLogStatus.ACCEPTED) || status.equals(TrackingLogStatus.REJECTED)) {
				boolean hasResolution = resolution != null && !resolution.isBlank();
				super.state(context, hasResolution, "Resolution", "Resolution can not be empty for finished trackingLogs (REJECTED or ACCEPTED)");
			}

			if (trackingLog.getLastUpdateMoment().compareTo(trackingLog.getClaim().getRegistrationMoment()) < 0)
				super.state(context, false, "LastUpdateMoment", "To create a trackingLog, the claim must have been created before");

			if (trackingLog.getClaim().isDraftMode())
				super.state(context, trackingLog.isDraftMode(), "DrafMode", "Before publishing a trackingLog the claim must be published");

			boolean morePercentage = true;

			if (trackingLog.getLastUpdateMoment() != null && trackingLog.getResolutionPercentage() != null) {
				List<TrackingLog> beforeActual = this.trackingLogsRepository.findOtherTrackingLogsBeforeCurrentOrderByPercentage(trackingLog.getClaim().getId(), trackingLog.getId(), trackingLog.getLastUpdateMoment());

				if (!beforeActual.isEmpty()) {
					TrackingLog previous = beforeActual.get(0);

					if (trackingLog.getResolutionPercentage() != 100.00) {
						morePercentage = trackingLog.getResolutionPercentage() > previous.getResolutionPercentage();
						super.state(context, morePercentage, "ResolutionPercentage", "The new percentage must be greater than the previous one");

					}

					Long maxComplete = this.trackingLogsRepository.findNumberOtherTrackingLogsFinishedBeforeCurrent(trackingLog.getClaim().getId(), trackingLog.getId(), trackingLog.getLastUpdateMoment());

					if (percentage.equals(100.00))
						if (maxComplete == 1) {
							super.state(context, status.equals(previous.getStatus()), "Status",
								"The status of the new tracking log with a 100% resolution percentage must match the status of the previous tracking log that also has a 100% resolution percentage ");
							super.state(context, !trackingLog.getClaim().isDraftMode() && previous.getResolutionPercentage().equals(100.00), "DraftMode", "You cannot create two tracking logs with a 100% resolution if the claim has not been published");
							super.state(context, !trackingLog.getClaim().isDraftMode() && previous.getResolutionPercentage().equals(100.00) && !previous.isDraftMode(), "DraftMode",
								"The other tracking log whit 100% of resolution percentage must be published");
						} else if (maxComplete >= 2)
							super.state(context, false, "ResolutionPercentage", "No additional tracking logs with a 100% resolution can be created");
				}
			}
		}

		return !super.hasErrors(context);
	}

}
