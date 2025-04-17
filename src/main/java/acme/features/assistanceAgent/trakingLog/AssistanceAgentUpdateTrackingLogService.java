
package acme.features.assistanceAgent.trakingLog;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.trackingLogs.TrackingLog;
import acme.entities.trackingLogs.TrackingLogStatus;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentUpdateTrackingLogService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		TrackingLog trackingLog;
		int id;
		AssistanceAgent assistanceAgent;
		Claim claim;

		id = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(id);
		claim = this.repository.findClaimByTrackingLogId(trackingLog.getId());

		assistanceAgent = trackingLog == null ? null : trackingLog.getClaim().getAssistanceAgent();

		status = claim != null && claim.isDraftMode() && super.getRequest().getPrincipal().hasRealm(assistanceAgent) && trackingLog != null && trackingLog.isDraftMode();

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		TrackingLog trackingLog;
		int id;

		Date moment;

		moment = MomentHelper.getCurrentMoment();

		id = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(id);
		trackingLog.setLastUpdateMoment(moment);

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "step", "resolutionPercentage", "status", "resolution");
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		Collection<TrackingLogStatus> statuses = Arrays.asList(TrackingLogStatus.values());
		TrackingLogStatus status = super.getRequest().getData("status", TrackingLogStatus.class);
		Double percentage = super.getRequest().getData("resolutionPercentage", Double.class);
		Integer trackingLogId = super.getRequest().getData("id", int.class);

		Claim claim = trackingLog.getClaim();
		if (claim == null) {
			super.state(false, "claim", "acme.validation.trackingLog.noClaim.message");
			return;
		}

		super.state(status != null && statuses.contains(status), "status", "acme.validation.trackingLog.invalidStatus");

		if (percentage != null && percentage.equals(100.00) && TrackingLogStatus.PENDING.equals(status))
			super.state(false, "resolutionPercentage", "acme.validation.trackingLog.wrongStatus.message");

		Collection<TrackingLog> trackingLogs = this.repository.findTrackingLogsByClaimIdExcludingOne(claim.getId(), trackingLogId);

		if (!trackingLogs.isEmpty() && percentage != null) {
			Double minPercentage = trackingLogs.stream().findFirst().map(t -> t.getResolutionPercentage()).orElse(0.00);
			long fullCount = trackingLogs.stream().filter(t -> t.getResolutionPercentage() == 100.00).count();

			if (fullCount == 0)
				super.state(percentage > minPercentage, "resolutionPercentage", "acme.validation.trackingLog.resolutionPercentage2.message");
			else if (fullCount == 1) {
				TrackingLog finishedLog = trackingLogs.stream().findFirst().orElse(null);
				if (finishedLog != null && status != null)
					super.state(percentage.equals(100.00) && status.equals(finishedLog.getStatus()) && !claim.isDraftMode(), "status", "acme.validation.trackingLog.status2.message");
			} else if (fullCount >= 2)
				super.state(false, "resolutionPercentage", "acme.validation.trackingLog.resolutionPercentage2.message");
		}
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		SelectChoices statuses;
		Dataset dataset;

		dataset = super.unbindObject(trackingLog, "step", "resolutionPercentage", "status", "resolution");

		statuses = SelectChoices.from(TrackingLogStatus.class, trackingLog.getStatus());
		dataset.put("statuses", statuses);
		dataset.put("masterId", trackingLog.getClaim().getId());
		dataset.put("draftMode", trackingLog.isDraftMode());

		super.getResponse().addData(dataset);
	}
}
