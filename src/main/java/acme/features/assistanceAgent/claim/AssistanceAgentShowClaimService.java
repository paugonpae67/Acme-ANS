
package acme.features.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimType;
import acme.entities.legs.Leg;
import acme.entities.trackingLogs.TrackingLogStatus;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentShowClaimService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		if (!super.getRequest().getMethod().equals("GET") || super.getRequest().getMethod().equals("GET") && !super.getRequest().hasData("id", int.class)) {
			super.getResponse().setAuthorised(false);
			return;
		}

		boolean status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

		if (!status) {
			super.getResponse().setAuthorised(false);
			return;
		}

		Integer claimId = super.getRequest().getData("id", Integer.class);
		if (claimId == null) {
			super.getResponse().setAuthorised(false);
			return;
		}

		Claim claim = this.repository.findClaimById(claimId);
		if (claim == null) {
			super.getResponse().setAuthorised(false);
			return;
		}

		int assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		status = assistanceAgentId == claim.getAssistanceAgent().getId();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Claim claim;
		int id;

		id = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(id);

		super.getBuffer().addData(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		int assistanceAgentId;
		Collection<Leg> legs;
		SelectChoices choicesLeg;
		Dataset dataset;
		SelectChoices types;
		TrackingLogStatus status;

		types = SelectChoices.from(ClaimType.class, claim.getType());
		status = claim.getStatus();

		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		AssistanceAgent assistanceAgent = this.repository.findAssistanceAgentById(assistanceAgentId);
		legs = this.repository.findAllPublishedLegs(claim.getRegistrationMoment(), assistanceAgent.getAirline().getId());

		choicesLeg = SelectChoices.from(legs, "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "type", "draftMode", "leg");
		dataset.put("types", types);
		dataset.put("leg", choicesLeg.getSelected().getKey());
		dataset.put("legs", choicesLeg);
		dataset.put("status", status);

		super.getResponse().addData(dataset);
	}

}
