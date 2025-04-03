
package acme.features.assistanceAgent.trakingLog;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
public class AssistanceAgentUpdateTrackingLogService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		TrackingLog trackingLog;
		AssistanceAgent assistanceAgent;

		masterId = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(masterId);
		assistanceAgent = trackingLog == null ? null : trackingLog.getClaim().getAssistanceAgent();
		status = super.getRequest().getPrincipal().hasRealm(assistanceAgent) || trackingLog != null;

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
		Claim claim;
		int id;

		claim = this.repository.findClaimById(trackingLog.getClaim().getId());
		super.bindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution");
		trackingLog.setClaim(claim);

	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		boolean validation;

		if (trackingLog.getResolutionPercentage() < 100.0) {
			validation = trackingLog.getStatus().equals(TrackingLogStatus.PENDING);
			super.state(validation, "status", "assistanceAgent.trackingLog.form.error.badStatus.notPending");
		} else {
			validation = !trackingLog.getStatus().equals(TrackingLogStatus.PENDING);
			super.state(validation, "status", "assistanceAgent.trackingLog.form.error.badStatus.notFinished");
		}
		if (trackingLog.getStatus().equals(TrackingLogStatus.PENDING)) {
			validation = trackingLog.getResolution() == null || trackingLog.getResolution().isBlank();
			super.state(validation, "resolution", "assistanceAgent.trackingLog.form.error.badResolution.notPercentage");
		} else {
			validation = trackingLog.getResolution() != null && !trackingLog.getResolution().isBlank();
			super.state(validation, "resolution", "assistanceAgent.trackingLog.form.error.badResolution.notPercentage2");
		}
		TrackingLog highestTrackingLog;
		Optional<List<TrackingLog>> trackingLogs = this.repository.findLatestTrackingLogByClaim(trackingLog.getClaim().getId());
		if (trackingLogs.isPresent() && trackingLogs.get().size() > 0) {
			highestTrackingLog = trackingLogs.get().get(0);
			long completedTrackingLogs = trackingLogs.get().stream().filter(t -> t.getResolutionPercentage() == 100).count();
			if (highestTrackingLog.getId() != trackingLog.getId())
				if (highestTrackingLog.getResolutionPercentage() == 100 && trackingLog.getResolutionPercentage() == 100) {
					validation = !highestTrackingLog.isDraftMode() && completedTrackingLogs < 2;
					super.state(validation, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.maxcompleted");
				} else {
					validation = highestTrackingLog.getResolutionPercentage() < trackingLog.getResolutionPercentage();
					super.state(validation, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.badPercentage");
				}
		}

	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {

		Collection<Claim> claims;
		SelectChoices statusChoices;
		SelectChoices claimChoices;
		Dataset dataset;
		int assistanceAgentId;
		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		statusChoices = SelectChoices.from(TrackingLogStatus.class, trackingLog.getStatus());

		claims = this.repository.findClaimByAssistanceAgent(assistanceAgentId);
		claimChoices = SelectChoices.from(claims, "id", trackingLog.getClaim());

		dataset = super.unbindObject(trackingLog, "claim", "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution", "draftMode", "id");
		dataset.put("statusChoices", statusChoices);
		dataset.put("claimChoices", claimChoices);

		super.getResponse().addData(dataset);

	}

}
