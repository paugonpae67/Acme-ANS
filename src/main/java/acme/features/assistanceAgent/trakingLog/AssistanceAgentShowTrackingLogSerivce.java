
package acme.features.assistanceAgent.trakingLog;

import java.util.Collection;

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
	public void unbind(final TrackingLog trackingLog) {
		Collection<Claim> claims;
		SelectChoices statuses;
		SelectChoices choiseClaims;
		Dataset dataset;
		int assistanceAgentId;

		statuses = SelectChoices.from(TrackingLogStatus.class, trackingLog.getStatus());
		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();

		claims = this.repository.findClaimByAssistanceAgent(assistanceAgentId);
		choiseClaims = SelectChoices.from(claims, "id", trackingLog.getClaim());

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution", "draftMode", "claim");
		dataset.put("statuses", statuses);
		dataset.put("claim", choiseClaims.getSelected().getKey());
		dataset.put("claims", choiseClaims);
		super.getResponse().addData(dataset);
	}

}
