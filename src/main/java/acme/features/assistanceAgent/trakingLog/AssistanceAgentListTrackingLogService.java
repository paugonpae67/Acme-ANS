
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
		if (!super.getRequest().getMethod().equals("GET") || super.getRequest().getMethod().equals("GET") && !super.getRequest().hasData("masterId", Integer.class)) {
			super.getResponse().setAuthorised(false);
			return;
		} else {
			boolean status;
			Integer masterId;
			Claim claim;

			masterId = super.getRequest().getData("masterId", Integer.class);
			if (masterId == null) {
				super.getResponse().setAuthorised(false);
				return;
			}

			claim = this.repository.findClaimById(masterId);
			status = claim != null;

			status = status && super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

			int assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
			status = status && assistanceAgentId == claim.getAssistanceAgent().getId();

			super.getResponse().setAuthorised(status);
		}

	}

	@Override
	public void load() {
		Collection<TrackingLog> trackingLogs;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		trackingLogs = this.repository.findTrackingLogOfClaimOrderByPercentage(masterId);

		super.getBuffer().addData(trackingLogs);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status");
		super.addPayload(dataset, trackingLog, "resolution");
		Claim claim;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		claim = this.repository.findClaimById(masterId);
		dataset.put("claim", claim);

		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<TrackingLog> trackingLog) {
		int masterId;
		boolean showCreate;

		masterId = super.getRequest().getData("masterId", int.class);

		Long maximumTrackingLogs = this.repository.findTrackingLogs100PercentageByMasterId(masterId).stream().count();
		showCreate = maximumTrackingLogs < 2;

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addGlobal("showCreate", showCreate);
	}

}
