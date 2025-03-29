
package acme.features.technicians.involvedIn;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.InvolvedIn;
import acme.entities.aircrafts.Task;
import acme.entities.aircrafts.TaskType;
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
		Dataset dataset;
		SelectChoices choices;
		Task task = involvedIn.getTask();

		choices = SelectChoices.from(TaskType.class, task.getType());
		dataset = super.unbindObject(involvedIn, "task");
		dataset.put("description", involvedIn.getTask().getDescription());
		dataset.put("type", choices.getSelected().getKey());
		dataset.put("types", choices);
		dataset.put("priority", involvedIn.getTask().getPriority());
		dataset.put("estimatedDuration", involvedIn.getTask().getEstimatedDuration());

		super.getResponse().addData(dataset);
	}

}
