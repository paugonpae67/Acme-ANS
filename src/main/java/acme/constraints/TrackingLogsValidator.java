
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

		boolean result = false;
		if (trackingLog == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			List<TrackingLog> latestTrackingLogOpt = this.trackingLogsRepository.findLatestTrackingLogByClaim(trackingLog.getClaim().getId()).get();

			if (latestTrackingLogOpt.get(0).getResolutionPercentage() > trackingLog.getResolutionPercentage())
				super.state(context, false, "*", "javax.validation.constraints.not-less-percentage.message");

			if ((trackingLog.getStatus().equals(TrackingLogStatus.ACCEPTED) || trackingLog.getStatus().equals(TrackingLogStatus.REJECTED)) && trackingLog.getResolutionPercentage() != 100)
				super.state(context, false, "*", "javax.validation.constraints.not-coherence.message");
			if (!trackingLog.getStatus().equals(TrackingLogStatus.PENDING) && (trackingLog.getResolution() == null || trackingLog.getResolution().isEmpty()))
				super.state(context, false, "*", "javax.validation.constraints.must-be-written-resolution.message");
			if (trackingLog.getResolution() != null)
				if (trackingLog.getStatus().equals(TrackingLogStatus.PENDING) && trackingLog.getResolutionPercentage() == 100)
					super.state(context, false, "*", "javax.validation.constraints.not-coherence.message");
		}
		result = !super.hasErrors(context);

		return result;
	}
}
