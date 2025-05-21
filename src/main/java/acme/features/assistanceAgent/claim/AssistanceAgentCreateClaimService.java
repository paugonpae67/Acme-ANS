
package acme.features.assistanceAgent.claim;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimType;
import acme.entities.legs.Leg;
import acme.entities.trackingLogs.TrackingLogStatus;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentCreateClaimService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		if (super.getRequest().getMethod().equals("GET") && super.getRequest().hasData("id", int.class))
			status = false;
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
		Date registrationMoment;
		AssistanceAgent assistanceAgent = (AssistanceAgent) super.getRequest().getPrincipal().getActiveRealm();

		registrationMoment = MomentHelper.getCurrentMoment();

		claim = new Claim();
		claim.setRegistrationMoment(registrationMoment);
		claim.setPassengerEmail("");
		claim.setDescription("");
		claim.setAssistanceAgent(assistanceAgent);
		claim.setType(ClaimType.FLIGHT_ISSUES);

		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		int legId;
		Leg leg;
		Date currentMoment;

		currentMoment = MomentHelper.getCurrentMoment();
		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegByLegId(legId);
		super.bindObject(claim, "passengerEmail", "description", "type");
		claim.setRegistrationMoment(currentMoment);
		if (leg != null)
			claim.setLeg(leg);
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
		boolean isCorrectLeg = true;
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
			isCorrectLeg = legs.contains(leg);
		}

		super.state(isCorrectType, "type", "acme.validation.claim.form.error.type");
		super.state(isCorrectLeg, "leg", "acme.validation.claim.form.error.leg");
		super.state(isNullLeg, "leg", "acme.validation.claim.form.error.leg2");
	}

	@Override
	public void perform(final Claim claim) {
		claim.setDraftMode(true);
		this.repository.save(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		AssistanceAgent assistanceAgent;
		Collection<Leg> legs;
		SelectChoices choices;
		SelectChoices choices2;
		TrackingLogStatus status;
		Dataset dataset;
		int agentId;

		choices = SelectChoices.from(ClaimType.class, claim.getType());
		agentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		assistanceAgent = this.repository.findAssistanceAgentById(agentId);
		status = claim.getStatus();

		legs = this.repository.findAllPublishedLegs(claim.getRegistrationMoment(), assistanceAgent.getAirline().getId());
		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description");
		dataset.put("types", choices);

		choices2 = SelectChoices.from(legs, "flightNumber", claim.getLeg());
		dataset.put("leg", choices2.getSelected().getKey());
		dataset.put("legs", choices2);

		dataset.put("status", status);

		super.getResponse().addData(dataset);
	}

}
