
package acme.constraints;

import java.util.List;
import java.util.Optional;

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

		if (trackingLog == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		}

		Optional<List<TrackingLog>> latestTrackingLogsOpt = this.trackingLogsRepository.findLatestTrackingLogByClaim(trackingLog.getClaim().getId());
		TrackingLog latestTrackingLog = latestTrackingLogsOpt.orElse(List.of()).stream().findFirst().orElse(null);

		//if (latestTrackingLog != null && latestTrackingLog.getResolutionPercentage() >= trackingLog.getResolutionPercentage())
		//	super.state(context, false, "*", "javax.validation.constraints.not-less-percentage.message");

		boolean isAcceptedOrRejected = trackingLog.getStatus() == TrackingLogStatus.ACCEPTED || trackingLog.getStatus() == TrackingLogStatus.REJECTED;
		if (isAcceptedOrRejected && trackingLog.getResolutionPercentage() != 100)
			super.state(context, false, "*", "javax.validation.constraints.not-coherence.message");

		boolean isResolutionRequired = trackingLog.getStatus() != TrackingLogStatus.PENDING && (trackingLog.getResolution() == null || trackingLog.getResolution().isEmpty());
		if (isResolutionRequired)
			super.state(context, false, "*", "javax.validation.constraints.must-be-written-resolution.message");

		boolean isInvalidPendingResolution = trackingLog.getStatus() == TrackingLogStatus.PENDING && trackingLog.getResolutionPercentage() == 100;
		if (isInvalidPendingResolution)
			super.state(context, false, "*", "javax.validation.constraints.not-coherence.message");

		return !super.hasErrors(context);
	}
}
