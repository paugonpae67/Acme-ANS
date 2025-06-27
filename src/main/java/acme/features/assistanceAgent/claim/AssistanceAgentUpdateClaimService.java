
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
public class AssistanceAgentUpdateClaimService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		Integer masterId;
		Claim claim;
		AssistanceAgent assistanceAgent;

		if (!super.getRequest().getMethod().equals("POST"))
			super.getResponse().setAuthorised(false);
		else if (super.getRequest().hasData("id")) {
			if (super.getRequest().getData("id", Integer.class) != null) {
				masterId = super.getRequest().getData("id", Integer.class);
				claim = this.repository.findClaimById(masterId);

				status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class) && claim != null;
				if (claim != null) {
					int assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
					status = status && assistanceAgentId == claim.getAssistanceAgent().getId();

					assistanceAgent = claim.getAssistanceAgent();

					status = status && claim.isDraftMode();

					Integer legId = super.getRequest().getData("leg", Integer.class);
					if (super.getRequest().getData("leg", Integer.class) != null) {
						if (legId != 0) {
							Leg leg = this.repository.findLegByLegId(legId);
							Collection<Leg> legs = this.repository.findAllPublishedLegs(claim.getRegistrationMoment(), assistanceAgent.getAirline().getId());

							status = status && legs.contains(leg);
							status = status && leg != null && !leg.isDraftMode();
						}
					} else {
						super.getResponse().setAuthorised(false);
						return;
					}

				} else {
					super.getResponse().setAuthorised(false);
					return;
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
		int legId;
		Leg leg;

		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegByLegId(legId);
		super.bindObject(claim, "passengerEmail", "description", "type");
		claim.setLeg(leg);

	}

	@Override
	public void validate(final Claim claim) {
		if (!claim.isDraftMode())
			super.state(false, "*", "assistanceAgent.claim.form.error.draftMode");
	}

	@Override
	public void perform(final Claim claim) {
		Date currentMoment;

		currentMoment = MomentHelper.getCurrentMoment();
		claim.setRegistrationMoment(currentMoment);
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
		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "draftMode");
		dataset.put("types", choices);

		choices2 = SelectChoices.from(legs, "flightNumber", claim.getLeg());
		dataset.put("leg", choices2.getSelected().getKey());
		dataset.put("legs", choices2);

		dataset.put("status", status);

		super.getResponse().addData(dataset);
	}

}
