
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
		boolean status;
		if (!super.getRequest().getMethod().equals("GET") || super.getRequest().getMethod().equals("GET") && (super.getRequest().hasData("id", int.class) || super.getRequest().hasData("masterId", int.class))) {
			super.getResponse().setAuthorised(false);
			return;
		} else {
			status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

			super.getResponse().setAuthorised(status);
		}

	}

	@Override
	public void load() {
		Collection<Claim> claims;
		int assistanceAgentId;

		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();

		claims = this.repository.findCompletedClaimsByAssistanceAgent(assistanceAgentId).stream().filter(x -> x.getStatus().equals(TrackingLogStatus.ACCEPTED) || x.getStatus().equals(TrackingLogStatus.REJECTED)).toList();

		super.getBuffer().addData(claims);
	}

	@Override
	public void unbind(final Claim claim) {
		Dataset dataset;
		TrackingLogStatus status;

		status = claim.getStatus();
		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "type", "status");

		dataset.put("leg.flightNumber", claim.getLeg().getFlightNumber());

		dataset.put("status", status);
		super.addPayload(dataset, claim, "description");

		super.getResponse().addData(dataset);
	}

}
