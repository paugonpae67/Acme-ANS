
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
public class AssistanceAgentShowTrackingLogSerivce extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		if (!super.getRequest().getMethod().equals("GET") || super.getRequest().getMethod().equals("GET") && !super.getRequest().hasData("id", Integer.class)) {
			super.getResponse().setAuthorised(false);
			return;
		}

		boolean status;
		TrackingLog trackingLog;
		Integer id;
		Claim claim;

		id = super.getRequest().getData("id", Integer.class);
		if (id == null) {
			super.getResponse().setAuthorised(false);
			return;
		}
		trackingLog = this.repository.findTrackingLogById(id);
		if (trackingLog == null) {
			super.getResponse().setAuthorised(false);
			return;
		}

		claim = this.repository.findClaimByTrackingLogId(trackingLog.getId());

		status = claim != null && super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);
		int assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		status = status && assistanceAgentId == claim.getAssistanceAgent().getId();

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
	public void unbind(final TrackingLog trackingLog) {
		SelectChoices statuses;
		Dataset dataset;

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "resolution", "draftMode");

		statuses = SelectChoices.from(TrackingLogStatus.class, trackingLog.getStatus());
		dataset.put("statuses", statuses);
		dataset.put("masterId", trackingLog.getClaim().getId());
		dataset.put("claimDraftMode", trackingLog.getClaim().isDraftMode());
		super.getResponse().addData(dataset);
	}

}
