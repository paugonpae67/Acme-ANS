
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
		} else {

			if (trackingLog.getResolutionPercentage() != 100)
				super.state(context, trackingLog.getStatus().equals(TrackingLogStatus.PENDING), "Status", "El estado debe de ser PENDING");
			else
				super.state(context, !trackingLog.getStatus().equals(TrackingLogStatus.PENDING), "Status", "El estado debe de ser ACCEPTED or REJECTED");

			if (trackingLog.getStatus().equals(TrackingLogStatus.ACCEPTED) || trackingLog.getStatus().equals(TrackingLogStatus.REJECTED))
				super.state(context, !trackingLog.getResolution().isEmpty() || trackingLog.getResolution() != null, "Resloution", "El campo resolution no puede estar vacío para trackingLogs terminados");

			Optional<List<TrackingLog>> latestTrackingLogsOpt = this.trackingLogsRepository.findLatestTrackingLogByClaimDraft(trackingLog.getClaim().getId());
			TrackingLog latestTrackingLog = latestTrackingLogsOpt.orElse(List.of()).stream().findFirst().orElse(null);
			if (!latestTrackingLogsOpt.isEmpty()) {
				Double minPercentage = latestTrackingLogsOpt.orElse(List.of()).stream().map(TrackingLog::getResolutionPercentage).min(Double::compare).orElse(0.00);
				Long maximumTrackingLogs = latestTrackingLogsOpt.orElse(List.of()).stream().filter(x -> x.getResolutionPercentage().equals(100.00)).count();
				if (Long.valueOf(0).equals(maximumTrackingLogs))
					super.state(context, trackingLog.getResolutionPercentage() >= minPercentage, "Percentage", "El porcentaje debe de ser ascendente");
				else if (Long.valueOf(1).equals(maximumTrackingLogs)) {
					super.state(context, trackingLog.getResolutionPercentage().equals(100.00) && trackingLog.getStatus().equals(latestTrackingLog.getStatus()), "Status", "El estado del nuevo tracking log debe de coincidir");
					super.state(context, !trackingLog.getClaim().isDraftMode() && latestTrackingLog.getResolutionPercentage().equals(100.00), "draftMode", "No se puede crear dos trackingLog con 100% si la claim no se ha publicado");
				} else if (Long.valueOf(2).equals(maximumTrackingLogs))
					super.state(context, false, "Forbidden", "No se pueden crear más trackingLogs");
			}
		}
		return !super.hasErrors(context);
	}
}
