
package acme.constraints;

import java.util.List;
import java.util.Objects;
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

		Double percentage = trackingLog.getResolutionPercentage();
		TrackingLogStatus status = trackingLog.getStatus();
		String resolution = trackingLog.getResolution();

		if (percentage == null) {
			super.state(context, false, "ResolutionPercentage", "El porcentaje no puede ser nulo");
			return false;
		}

		if (status == null) {
			super.state(context, false, "Status", "El estado no puede ser nulo");
			return false;
		}

		Optional<List<TrackingLog>> trackingLogs = this.trackingLogsRepository.findLatestTrackingLogByClaim(trackingLog.getClaim().getId());
		Long countPercentage = trackingLogs.orElse(List.of()).stream().filter(x -> !Objects.equals(x.getId(), trackingLog.getId())).filter(x -> Objects.equals(x.getResolutionPercentage(), trackingLog.getResolutionPercentage())).count();

		if (countPercentage > 0)
			super.state(context, false, "ResolutionPercentage", "No puede haber dos trackingLogs con el mismo porcentaje");

		if (percentage != 100)
			super.state(context, status.equals(TrackingLogStatus.PENDING), "Status", "El estado debe de ser PENDING");
		else
			super.state(context, !status.equals(TrackingLogStatus.PENDING), "Status", "El estado debe de ser ACCEPTED or REJECTED");

		if (status.equals(TrackingLogStatus.ACCEPTED) || status.equals(TrackingLogStatus.REJECTED)) {
			boolean hasResolution = resolution != null && !resolution.isBlank();
			super.state(context, hasResolution, "Resolution", "El campo resolution no puede estar vacío para trackingLogs terminados");
		}

		List<TrackingLog> latestTrackingLogs = this.trackingLogsRepository.findOtherTrackingLogsOrderedByLastUpdateDraft(trackingLog.getClaim().getId(), trackingLog.getId());
		TrackingLog latestTrackingLog = latestTrackingLogs.stream().findFirst().orElse(null);

		if (!latestTrackingLogs.isEmpty()) {

			Double minPercentage = latestTrackingLogs.stream().map(TrackingLog::getResolutionPercentage).filter(p -> p != null).min(Double::compare).orElse(0.00);

			Long maxComplete = latestTrackingLogs.stream().filter(x -> x.getResolutionPercentage() != null && x.getResolutionPercentage().equals(100.00)).count();

			if (maxComplete == 0)
				super.state(context, percentage >= minPercentage, "resolutionPercentage", "El porcentaje debe de ser ascendente");
			else if (maxComplete == 1 && latestTrackingLog != null) {
				super.state(context, percentage.equals(100.00) && status.equals(latestTrackingLog.getStatus()), "status", "El estado del nuevo tracking log debe de coincidir");
				super.state(context, !trackingLog.getClaim().isDraftMode() && latestTrackingLog.getResolutionPercentage().equals(100.00), "draftMode", "No se puede crear dos trackingLog con 100% si la claim no se ha publicado");
			} else if (maxComplete >= 2)
				super.state(context, false, "resolutionPercentage", "No se pueden crear más trackingLogs con 100%");
		}

		return !super.hasErrors(context);
	}

}
