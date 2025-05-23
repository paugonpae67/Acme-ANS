
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
import acme.entities.trackingLogs.TrackingLogStatus;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentPublishClaimService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		if (!super.getRequest().getMethod().equals("POST"))
			super.getResponse().setAuthorised(false);
		else {
			status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

			if (super.getRequest().hasData("id")) {
				Integer legId = super.getRequest().getData("leg", Integer.class);
				if (legId == null || legId != 0) {
					Leg leg = this.repository.findLegById(legId);
					status = status && leg != null && !leg.isDraftMode();
				}
			}
			super.getResponse().setAuthorised(status);
		}
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
	public void bind(final Claim claim) {
		super.bindObject(claim, "passengerEmail", "description", "type", "leg");
	}

	@Override
	public void validate(final Claim claim) {

		Collection<Leg> legs;
		Collection<ClaimType> types;
		ClaimType type;
		int legId;
		Leg leg;
		int agentId;
		AssistanceAgent assistanceAgent;
		boolean legCorrect = true;
		boolean isNullLeg = true;
		boolean isCorrectType = false;

		String typeStr = super.getRequest().getData("type", String.class);

		for (ClaimType ct : ClaimType.values())
			if (ct.name().equals(typeStr)) {
				type = ct;
				isCorrectType = true;
				break;
			}

		agentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		assistanceAgent = this.repository.findAssistanceAgentById(agentId);
		legs = this.repository.findAllPublishedLegs(claim.getRegistrationMoment(), assistanceAgent.getAirline().getId());

		if (legs.isEmpty())
			isNullLeg = false;
		else {
			legId = super.getRequest().getData("leg", int.class);
			leg = this.repository.findLegById(legId);
			legCorrect = legs.contains(leg);
		}

		super.state(isCorrectType, "type", "acme.validation.claim.form.error.type");
		super.state(legCorrect, "leg", "acme.validation.claim.form.error.leg");
		super.state(isNullLeg, "leg", "acme.validation.claim.form.error.leg2");
		super.state(claim.isDraftMode(), "draftMode", "acme.validation.claim.form.error.draftMode");
	}

	@Override
	public void perform(final Claim claim) {
		claim.setDraftMode(false);
		this.repository.save(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		int assistanceAgentId;
		Collection<Leg> legs;
		SelectChoices choicesLeg;
		Dataset dataset;
		SelectChoices types;
		TrackingLogStatus status;

		status = claim.getStatus();

		types = SelectChoices.from(ClaimType.class, claim.getType());

		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		AssistanceAgent assistanceAgent = this.repository.findAssistanceAgentById(assistanceAgentId);
		Airport a = this.repository.findAirportOfAirlineByAssistanceAgentId(assistanceAgent.getAirline().getId());
		legs = this.repository.findLegByAirport(a.getId());

		choicesLeg = SelectChoices.from(legs, "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "type", "draftMode");
		dataset.put("type", types.getSelected().getKey());
		dataset.put("types", types);
		dataset.put("leg", choicesLeg.getSelected().getKey());
		dataset.put("legs", choicesLeg);
		dataset.put("status", status);
		super.getResponse().addData(dataset);
	}
}
