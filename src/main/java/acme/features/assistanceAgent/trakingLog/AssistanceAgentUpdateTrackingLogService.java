
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
		TrackingLog trackingLog;
		int id;
		AssistanceAgent assistanceAgent;

		id = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(id);
		assistanceAgent = trackingLog == null ? null : trackingLog.getClaim().getAssistanceAgent();
		status = super.getRequest().getPrincipal().hasRealm(assistanceAgent);

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
		super.bindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution", "claim");

	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		boolean valid;

		if (trackingLog.getResolutionPercentage() < 100.0) {
			valid = trackingLog.getStatus().equals(TrackingLogStatus.PENDING);
			super.state(valid, "status", "assistanceAgent.trackingLog.form.error.wrongStatus");
		} else {
			valid = !trackingLog.getStatus().equals(TrackingLogStatus.PENDING);
			super.state(valid, "status", "assistanceAgent.trackingLog.form.error.wrongStatus2");
		}
		if (trackingLog.getStatus().equals(TrackingLogStatus.ACCEPTED) || trackingLog.getStatus().equals(TrackingLogStatus.REJECTED)) {
			valid = trackingLog.getResolution() != null && !trackingLog.getResolution().isBlank();
			super.state(valid, "resolution", "assistanceAgent.trackingLog.form.error.resolutionNeeded");
		}

		TrackingLog lastTrackingLog;
		Optional<List<TrackingLog>> trackingLogs = this.repository.findLatestTrackingLogByClaim(trackingLog.getClaim().getId());
		if (trackingLogs.isPresent() && trackingLogs.get().size() > 0) {
			lastTrackingLog = trackingLogs.get().get(0);
			long completedTrackingLogs = trackingLogs.get().stream().filter(t -> t.getResolutionPercentage() == 100).count();
			if (lastTrackingLog.getId() != trackingLog.getId())
				if (lastTrackingLog.getResolutionPercentage() == 100 && trackingLog.getResolutionPercentage() == 100) {
					valid = !lastTrackingLog.isDraftMode() && completedTrackingLogs < 2;
					super.state(valid, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.complatePercentage");
				} else {
					valid = lastTrackingLog.getResolutionPercentage() < trackingLog.getResolutionPercentage();
					super.state(valid, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.wrongNewPercentage");
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
		SelectChoices statuses;
		SelectChoices claimChoices;
		Dataset dataset;
		int assistanceAgentId;
		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		statuses = SelectChoices.from(TrackingLogStatus.class, trackingLog.getStatus());

		claims = this.repository.findClaimByAssistanceAgent(assistanceAgentId);
		claimChoices = SelectChoices.from(claims, "id", trackingLog.getClaim());

		dataset = super.unbindObject(trackingLog, "claim", "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution", "draftMode", "id");
		dataset.put("statuses", statuses);
		dataset.put("status", statuses.getSelected().getKey());
		dataset.put("claimChoices", claimChoices);

		super.getResponse().addData(dataset);

	}
}
