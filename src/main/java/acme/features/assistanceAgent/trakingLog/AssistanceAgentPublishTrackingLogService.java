
package acme.features.assistanceAgent.trakingLog;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.trackingLogs.TrackingLog;
import acme.entities.trackingLogs.TrackingLogStatus;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentPublishTrackingLogService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		Claim claim;
		int id;
		TrackingLog trackingLog;

		id = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(id);

		claim = this.repository.findClaimByTrackingLogId(id);

		status = claim != null && !claim.isDraftMode() && super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent()) && trackingLog != null;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrackingLog trackingLog;
		int id;

		id = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(id);

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "step", "resolutionPercentage", "status", "resolution");
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		List<TrackingLog> trackingLogs = this.repository.findLatestTrackingLogByClaimExceptItself(trackingLog.getClaim().getId(), trackingLog.getId()).orElse(List.of());
		List<TrackingLog> beforeActual = trackingLogs.stream().filter(t -> t.getId() != trackingLog.getId()).filter(t -> !t.getLastUpdateMoment().after(trackingLog.getLastUpdateMoment())).collect(Collectors.toList());

		if (trackingLog.getResolutionPercentage() != null && trackingLog.getStatus() != null && trackingLog.getResolution() != null) {
			Double percentage = trackingLog.getResolutionPercentage();
			TrackingLogStatus status = trackingLog.getStatus();
			String resolution = trackingLog.getResolution();
			Long countPercentage = trackingLogs.stream().filter(x -> !x.getResolutionPercentage().equals(100.00)).filter(x -> !Objects.equals(x.getId(), trackingLog.getId()))
				.filter(x -> Objects.equals(x.getResolutionPercentage(), trackingLog.getResolutionPercentage())).count();

			if (countPercentage > 0)
				super.state(false, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.notSamePercentage");

			if (percentage != 100)
				super.state(status.equals(TrackingLogStatus.PENDING), "status", "assistanceAgent.trackingLog.form.error.statusWrongNotFinished");
			else
				super.state(!status.equals(TrackingLogStatus.PENDING), "status", "assistanceAgent.trackingLog.form.error.statusWrongFinished");

			if (status.equals(TrackingLogStatus.ACCEPTED) || status.equals(TrackingLogStatus.REJECTED)) {
				boolean hasResolution = resolution != null && !resolution.isBlank();
				super.state(hasResolution, "resolution", "assistanceAgent.trackingLog.form.error.resolutionNeeded");
			}

			if (!beforeActual.isEmpty()) {
				beforeActual.sort(Comparator.comparing(TrackingLog::getResolutionPercentage).reversed());
				if (percentage.equals(100.00)) {
					TrackingLog alreadyPublished = trackingLogs.stream().filter(x -> x.getResolutionPercentage() != null && x.getResolutionPercentage().equals(100.00)).filter(x -> x.getId() != trackingLog.getId()).filter(x -> !x.isDraftMode()).findFirst()
						.orElse(null);

					if (alreadyPublished != null) {
						boolean isAfter = trackingLog.getLastUpdateMoment().after(alreadyPublished.getLastUpdateMoment());
						super.state(isAfter, "*", "assistanceAgent.trackingLog.form.error.onlyOneCompleteAllowed");
					}
				}
			}

		}
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		trackingLog.setDraftMode(false);
		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		SelectChoices statuses;
		Dataset dataset;

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution");

		statuses = SelectChoices.from(TrackingLogStatus.class, trackingLog.getStatus());
		dataset.put("statuses", statuses);
		dataset.put("claimId", trackingLog.getClaim().getId());
		dataset.put("draftMode", trackingLog.isDraftMode());

		super.getResponse().addData(dataset);
	}

}
