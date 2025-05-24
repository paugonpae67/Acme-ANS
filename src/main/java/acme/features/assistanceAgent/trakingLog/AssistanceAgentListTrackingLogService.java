
package acme.features.assistanceAgent.trakingLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.trackingLogs.TrackingLog;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentListTrackingLogService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		if (!super.getRequest().getMethod().equals("GET") || super.getRequest().getMethod().equals("GET") && super.getRequest().hasData("id", Integer.class)) {
			super.getResponse().setAuthorised(false);
			return;
		} else {
			boolean status;
			int masterId;
			Claim claim;

			masterId = super.getRequest().getData("masterId", Integer.class);
			if (super.getRequest().getData("masterId", Integer.class) == null) {
				super.getResponse().setAuthorised(false);
				return;
			}
			claim = this.repository.findClaimById(masterId);
			if (claim == null) {
				super.getResponse().setAuthorised(false);
				return;
			}

			status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

			int agentId = super.getRequest().getPrincipal().getActiveRealm().getId();

			if (agentId != claim.getAssistanceAgent().getId())
				status = false;

			super.getResponse().setAuthorised(status);
		}

	}

	@Override
	public void load() {
		Collection<TrackingLog> trackingLogs;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		trackingLogs = this.repository.findTrackingLogOfClaimByPercentage(masterId);

		super.getBuffer().addData(trackingLogs);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status", "draftMode");
		super.addPayload(dataset, trackingLog, "resolution");

		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<TrackingLog> trackingLog) {
		int masterId;
		Claim claim;
		final boolean showCreate;

		masterId = super.getRequest().getData("masterId", int.class);
		claim = this.repository.findClaimById(masterId);
		Long maximumTrackingLogs = trackingLog.stream().filter(t -> t.getResolutionPercentage().equals(100.00)).count();
		showCreate = !claim.isDraftMode() && super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent()) && maximumTrackingLogs < 2;

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addGlobal("showCreate", showCreate);
	}

}
