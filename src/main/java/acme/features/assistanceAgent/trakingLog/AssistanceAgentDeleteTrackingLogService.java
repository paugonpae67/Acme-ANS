
package acme.features.assistanceAgent.trakingLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.trackingLogs.TrackingLog;
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
		AssistanceAgent agent;

		if (!super.getRequest().getMethod().equals("POST"))
			super.getResponse().setAuthorised(false);
		else {
			id = super.getRequest().getData("id", Integer.class);
			if (id == null) {
				super.getResponse().setAuthorised(false);
				return;
			}
			trackingLog = this.repository.findTrackingLogById(id);
			if (trackingLog != null) {
				claim = this.repository.findClaimByTrackingLogId(trackingLog.getId());

				status = claim != null && super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class) && trackingLog != null;

				int agentId = super.getRequest().getPrincipal().getActiveRealm().getId();

				int assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
				status = status && assistanceAgentId == claim.getAssistanceAgent().getId();

				agent = claim.getAssistanceAgent();

				status = status && claim.isDraftMode();

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
		Integer id;

		id = super.getRequest().getData("id", Integer.class);
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
	}
}
