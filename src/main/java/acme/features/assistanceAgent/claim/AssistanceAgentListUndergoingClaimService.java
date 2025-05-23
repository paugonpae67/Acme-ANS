
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
public class AssistanceAgentListUndergoingClaimService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		if (!super.getRequest().getMethod().equals("GET")) {
			super.getResponse().setAuthorised(false);
			return;
		}

		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Claim> claims;
		int assistanceAgentId;

		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		claims = this.repository.findCompletedClaimsByAssistanceAgent(assistanceAgentId).stream().filter(x -> x.getStatus() == TrackingLogStatus.PENDING).toList();

		super.getBuffer().addData(claims);
	}

	@Override
	public void unbind(final Claim claim) {
		Dataset dataset;
		TrackingLogStatus status;

		status = claim.getStatus();
		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "type", "status", "leg.flightNumber");
		dataset.put("status", status);
		super.addPayload(dataset, claim, "description", "leg.flightNumber");

		super.getResponse().addData(dataset);
	}

}
