
package acme.features.technicians.involvedIn;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.InvolvedIn;
import acme.entities.aircrafts.MaintenanceRecord;
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
		Dataset dataset;
		SelectChoices choices;
		Collection<Task> tasks;
		MaintenanceRecord maintenanceRecord = involvedIn.getMaintenanceRecord();

		tasks = this.repository.findTasksDisponibles();
		choices = SelectChoices.from(tasks, "description", involvedIn.getTask());

		dataset = super.unbindObject(involvedIn);
		dataset.put("tasks", choices);
		dataset.put("task", choices.getSelected().getKey());
		dataset.put("priority", involvedIn.getTask().getPriority());
		dataset.put("technician", involvedIn.getTask().getTechnician().getLicenseNumber());
		dataset.put("draftMode", maintenanceRecord.isDraftMode());
		super.getResponse().addData(dataset);
	}

}
