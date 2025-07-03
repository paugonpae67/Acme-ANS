
package acme.features.administrator.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.trackingLogs.TrackingLogStatus;

@GuiService
public class AdministratorClaimListService extends AbstractGuiService<Administrator, Claim> {

	@Autowired
	private AdministratorClaimRepository repository;


	@Override
	public void authorise() {
		if (!super.getRequest().getMethod().equals("GET")) {
			super.getResponse().setAuthorised(false);
			return;
		} else {
			super.getResponse().setAuthorised(true);
			return;
		}
	}

	@Override
	public void load() {
		Collection<Claim> claims = this.repository.findClaimPublished();
		super.getBuffer().addData(claims);
	}

	@Override
	public void unbind(final Claim claim) {

		Dataset dataset;
		TrackingLogStatus status = claim.getStatus();

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "type", "status");
		dataset.put("status", status);
		super.addPayload(dataset, claim, "description");

		super.getResponse().addData(dataset);
	}
}
