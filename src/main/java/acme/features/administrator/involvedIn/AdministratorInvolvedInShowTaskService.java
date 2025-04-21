
package acme.features.administrator.involvedIn;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.InvolvedIn;
import acme.entities.aircrafts.TaskType;

@GuiService
public class AdministratorInvolvedInShowTaskService extends AbstractGuiService<Administrator, InvolvedIn> {

	@Autowired
	private AdministratorInvolvedInRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		InvolvedIn involvedIn;
		int id;

		id = super.getRequest().getData("id", int.class);
		involvedIn = this.repository.findInvolvedInById(id);

		super.getBuffer().addData(involvedIn);
	}

	@Override
	public void unbind(final InvolvedIn involvedIn) {
		Dataset dataset;
		dataset = super.unbindObject(involvedIn);

		SelectChoices choices;
		choices = SelectChoices.from(TaskType.class, involvedIn.getTask().getType());

		dataset.put("ticker", involvedIn.getTask().getTicker());
		dataset.put("description", involvedIn.getTask().getDescription());
		dataset.put("priority", involvedIn.getTask().getPriority());
		dataset.put("estimatedDuration", involvedIn.getTask().getEstimatedDuration());
		dataset.put("type", choices.getSelected().getKey());
		dataset.put("types", choices);
		dataset.put("technician", involvedIn.getTask().getTechnician().getLicenseNumber());
		dataset.put("masterId", involvedIn.getMaintenanceRecord().getId());
		super.getResponse().addData(dataset);
	}
}
