
package acme.features.assistanceAgent.trakingLog;

import java.util.List;

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
		super.bindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution");
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		super.state(trackingLog.isDraftMode(), "draftMode", "assistanceAgent.trackingLog.form.error.draftMode");

		List<TrackingLog> trackingLogs = this.repository.findLatestTrackingLogByClaimExceptItself(trackingLog.getClaim().getId(), trackingLog.getId()).orElse(List.of());

		TrackingLog latestTrackingLog = trackingLogs.stream().findFirst().orElse(null);
		if (!trackingLog.getClaim().isDraftMode()) {
			if (!trackingLogs.isEmpty()) {
				Double minPercentage = trackingLogs.stream().map(TrackingLog::getResolutionPercentage).min(Double::compare).orElse(0.00);

				long maximumTrackingLogs = trackingLogs.stream().filter(x -> x.getResolutionPercentage().equals(100.00)).count();

				if (maximumTrackingLogs == 0) {
					TrackingLog minTrackingLog = trackingLogs.stream().filter(x -> x.getResolutionPercentage().equals(minPercentage)).findFirst().orElse(null);
					if (minTrackingLog.getLastUpdateMoment().compareTo(trackingLog.getLastUpdateMoment()) < 0)
						super.state(trackingLog.getResolutionPercentage() >= minPercentage, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.wrongNewPercentage");

					if (trackingLog.getResolutionPercentage() < 100.0) {
						boolean badStatus = !trackingLog.getStatus().equals(TrackingLogStatus.PENDING);
						super.state(!badStatus, "status", "assistanceAgent.trackingLog.form.error.wrongStatus");
					} else {
						boolean badStatus = trackingLog.getStatus().equals(TrackingLogStatus.PENDING);
						super.state(!badStatus, "status", "assistanceAgent.trackingLog.form.error.wrongStatus2");
					}

					if (!trackingLog.getStatus().equals(TrackingLogStatus.PENDING)) {
						boolean hasResolution = trackingLog.getResolution() != null && !trackingLog.getResolution().isBlank();
						super.state(hasResolution, "resolution", "assistanceAgent.trackingLog.form.error.resolutionNeeded");
					}

				} else if (maximumTrackingLogs == 1) {
					boolean badStatus = trackingLog.getStatus().equals(TrackingLogStatus.PENDING);
					super.state(!badStatus, "status", "assistanceAgent.trackingLog.form.error.wrongStatus2");

					super.state(trackingLog.getResolutionPercentage().equals(100.00) && trackingLog.getStatus().equals(latestTrackingLog.getStatus()), "status", "assistanceAgent.trackingLog.form.error.statusNewPercentageTotal");

					super.state(!trackingLog.getClaim().isDraftMode() && latestTrackingLog.getResolutionPercentage().equals(100.00), "draftMode", "No se puede crear dos trackingLog con 100% si la claim no se ha publicado.");
				} else if (maximumTrackingLogs >= 2)
					super.state(false, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.complatePercentage");
			}
		} else
			super.state(false, "draftMode", "assistanceAgent.trackingLog.form.error.draftModeClaim");
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

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution", "draftMode");

		statuses = SelectChoices.from(TrackingLogStatus.class, trackingLog.getStatus());
		dataset.put("statuses", statuses);
		dataset.put("claimId", trackingLog.getClaim().getId());
		dataset.put("draftMode", trackingLog.isDraftMode());

		super.getResponse().addData(dataset);
	}

}
