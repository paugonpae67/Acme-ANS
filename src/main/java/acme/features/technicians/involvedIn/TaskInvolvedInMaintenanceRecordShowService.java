
package acme.features.technicians.involvedIn;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.InvolvedIn;
import acme.entities.aircrafts.Task;
import acme.realms.Technician;

@GuiService
public class TaskInvolvedInMaintenanceRecordShowService extends AbstractGuiService<Technician, InvolvedIn> {

	@Autowired
	private TaskInvolvedInMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int id;
		InvolvedIn involvedIn;

		id = super.getRequest().getData("id", int.class);
		involvedIn = this.repository.findInvolvedInById(id);
		status = involvedIn != null;

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
		Collection<Task> tasks;
		SelectChoices choices;
		Dataset dataset;

		tasks = this.repository.findAllInvolvedInMaintenanceRecord(involvedIn.getMaintenanceRecord().getId());
		choices = SelectChoices.from(tasks, "type", involvedIn.getTask());

		dataset = super.unbindObject(involvedIn, "");
		dataset.put("task", involvedIn.getTask().getId());
		dataset.put("tasks", choices);

		super.getResponse().addData(dataset);
	}

}
