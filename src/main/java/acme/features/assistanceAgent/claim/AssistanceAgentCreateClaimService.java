
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
		boolean status = false;

		String method = super.getRequest().getMethod();

		if ("GET".equals(method) && !super.getRequest().getData().isEmpty()) {
			super.getResponse().setAuthorised(false);
			return;
		}

		if ("POST".equals(method)) {
			Integer id = super.getRequest().getData("id", Integer.class);
			if (id == null || id != 0) {
				super.getResponse().setAuthorised(false);
				return;
			}
		}

		status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

		if (super.getRequest().hasData("id", Integer.class) && super.getRequest().hasData("leg", Integer.class)) {
			Integer legId = super.getRequest().getData("leg", Integer.class);

			if (legId == null) {
				super.getResponse().setAuthorised(false);
				return;
			}

			if (legId != 0) {
				AssistanceAgent assistanceAgent = (AssistanceAgent) super.getRequest().getPrincipal().getActiveRealm();
				Leg leg = this.repository.findLegById(legId);
				Collection<Leg> legs = this.repository.findAllPublishedLegs(MomentHelper.getCurrentMoment(), assistanceAgent.getAirline().getId());

				status = status && legs.contains(leg);
				status = status && leg != null && !leg.isDraftMode();

			}
		}

		super.getResponse().setAuthorised(status);
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
		Integer agentId;

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
