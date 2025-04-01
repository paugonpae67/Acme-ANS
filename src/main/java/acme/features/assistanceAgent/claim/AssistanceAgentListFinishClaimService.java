
package acme.features.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.trackingLogs.TrackingLogStatus;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentListFinishClaimService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Claim> claims;
		int assistanceAgentId;

		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();

		claims = this.repository.findCompletedClaimsByAssistanceAgent(assistanceAgentId).stream().filter(x -> x.getStatus() == TrackingLogStatus.ACCEPTED || x.getStatus() == TrackingLogStatus.REJECTED).toList();

		super.getBuffer().addData(claims);
	}

	@Override
	public void unbind(final Claim claim) {
		Dataset dataset;
		TrackingLogStatus status;

		status = claim.getStatus();
		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "type");
		dataset.put("status", status);
		super.addPayload(dataset, claim, "description", "leg.flightNumber");

		super.getResponse().addData(dataset);
	}

}
