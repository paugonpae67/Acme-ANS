
package acme.features.administrator.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimType;
import acme.entities.legs.Leg;
import acme.entities.trackingLogs.TrackingLogStatus;
import acme.features.assistanceAgent.claim.AssistanceAgentClaimRepository;

@GuiService
public class AdministratorClaimShowService extends AbstractGuiService<Administrator, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		if (!super.getRequest().getMethod().equals("GET"))
			super.getResponse().setAuthorised(false);
		else if (!super.getRequest().hasData("id", int.class))
			super.getResponse().setAuthorised(false);
		else if (super.getRequest().getData("id", int.class) == null)
			super.getResponse().setAuthorised(false);
		else {
			int id = super.getRequest().getData("id", int.class);
			Claim claim = this.repository.findClaimById(id);
			if (claim == null)
				super.getResponse().setAuthorised(false);
			else
				super.getResponse().setAuthorised(true);
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
	public void unbind(final Claim claim) {
		Collection<Leg> legs;
		SelectChoices choicesType;
		SelectChoices choicesLegs;
		Dataset dataset;
		TrackingLogStatus status;

		status = claim.getStatus();
		choicesType = SelectChoices.from(ClaimType.class, claim.getType());
		legs = this.repository.findAllLeg();
		choicesLegs = SelectChoices.from(legs, "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "type", "draftMode", "id");
		dataset.put("types", choicesType);
		dataset.put("leg", choicesLegs.getSelected().getKey());
		dataset.put("legs", choicesLegs);
		dataset.put("status", status);

		super.getResponse().addData(dataset);
	}

}
