
package acme.features.assistanceAgent.trakingLog;

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
public class AssistanceAgentDeleteTrackingLogService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		TrackingLog trackingLog;
		Integer id;
		Claim claim;

		if (!super.getRequest().getMethod().equals("POST"))
			super.getResponse().setAuthorised(false);
		else {
			id = super.getRequest().getData("id", Integer.class);
			if (super.getRequest().getData("id", Integer.class) == null || id != 0) {
				super.getResponse().setAuthorised(false);
				return;
			}
			trackingLog = this.repository.findTrackingLogById(id);
			if (trackingLog != null) {
				claim = this.repository.findClaimByTrackingLogId(trackingLog.getId());

				status = claim != null && super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class) && trackingLog != null;

				int agentId = super.getRequest().getPrincipal().getActiveRealm().getId();

				if (claim == null || agentId != claim.getAssistanceAgent().getId())
					status = false;

				super.getResponse().setAuthorised(status);
			} else {
				super.getResponse().setAuthorised(false);
				return;
			}

		}

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
		super.state(trackingLog.isDraftMode(), "draftMode", "acme.validation.trackingLog.draftMode.message");
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		this.repository.delete(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		SelectChoices statuses;
		Dataset dataset;

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution");

		statuses = SelectChoices.from(TrackingLogStatus.class, trackingLog.getStatus());
		dataset.put("statuses", statuses);
		dataset.put("masterId", trackingLog.getClaim().getId());
		dataset.put("draftMode", trackingLog.isDraftMode());

		super.getResponse().addData(dataset);
	}

}
