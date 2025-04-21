
package acme.features.administrator.involvedIn;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.InvolvedIn;

@GuiService
public class AdministratorInvolvedInListService extends AbstractGuiService<Administrator, InvolvedIn> {

	@Autowired
	private AdministratorInvolvedInRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int masterId;
		Collection<InvolvedIn> involvedIn;

		masterId = super.getRequest().getData("masterId", int.class);
		involvedIn = this.repository.findInvolvedInByMaintenanceRecord(masterId);
		super.getBuffer().addData(involvedIn);
	}

	@Override
	public void unbind(final InvolvedIn involvedIn) {
		Dataset dataset;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		dataset = super.unbindObject(involvedIn);
		dataset.put("task", involvedIn.getTask().getType());
		dataset.put("ticker", involvedIn.getTask().getTicker());
		dataset.put("priority", involvedIn.getTask().getPriority());
		super.addPayload(dataset, involvedIn);
		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addData(dataset);
	}

}
