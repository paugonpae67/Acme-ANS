
package acme.features.assistanceAgent.claim;

import java.util.Collection;

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
public class AssistanceAgentPublishClaimService extends AbstractGuiService<AssistanceAgent, Claim> {

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
		super.bindObject(claim, "passengerEmail", "description", "type", "leg");
	}

	@Override
	public void validate(final Claim claim) {
		super.state(claim.isDraftMode(), "*", "acme.validation.claim.form.error.draftMode");
	}

	@Override
	public void perform(final Claim claim) {
		claim.setDraftMode(false);
		claim.setRegistrationMoment(MomentHelper.getCurrentMoment());
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
		legs = this.repository.findAllPublishedLegs(claim.getRegistrationMoment(), assistanceAgent.getAirline().getId());

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
