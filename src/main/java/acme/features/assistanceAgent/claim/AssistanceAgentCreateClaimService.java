
package acme.features.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airports.Airport;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimType;
import acme.entities.legs.Leg;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentCreateClaimService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Claim claim;
		AssistanceAgent assistanceAgent;

		assistanceAgent = (AssistanceAgent) super.getRequest().getPrincipal().getActiveRealm();
		claim = new Claim();
		claim.setAssistanceAgent(assistanceAgent);
		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		int legId;
		Leg leg;

		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegByLegId(legId);
		super.bindObject(claim, "registrationMoment", "passengerEmail", "description", "type");
		claim.setLeg(leg);

	}

	@Override
	public void validate(final Claim claim) {
		;
	}

	@Override
	public void perform(final Claim claim) {
		this.repository.save(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		int assistanceAgentId;
		Collection<Leg> legs;
		SelectChoices choicesLeg;
		Dataset dataset;
		SelectChoices types;

		types = SelectChoices.from(ClaimType.class, claim.getType());

		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		AssistanceAgent assistanceAgent = this.repository.findAssistanceAgentById(assistanceAgentId);
		Airport a = this.repository.findAirportOfAirlineByAssistanceAgentId(assistanceAgent.getAirline().getId());
		legs = this.repository.findLegByAirport(a.getId());

		choicesLeg = SelectChoices.from(legs, "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "type", "leg");
		dataset.put("types", types);
		dataset.put("leg", choicesLeg.getSelected().getKey());
		dataset.put("legs", choicesLeg);

		super.getResponse().addData(dataset);
	}

}
