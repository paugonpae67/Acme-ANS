
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
public class TaskInvolvedInMaintenanceRecordCreateService extends AbstractGuiService<Technician, InvolvedIn> {

	@Autowired
	private TaskInvolvedInMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		InvolvedIn object;
		int masterId;
		MaintenanceRecord maintenanceRecord;

		masterId = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(masterId);

		object = new InvolvedIn();
		object.setTask(null);
		object.setMaintenanceRecord(maintenanceRecord);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final InvolvedIn involvedIn) {

		MaintenanceRecord maintenanceRecord;
		Task task;
		int taskId;

		super.bindObject(involvedIn, "");

		taskId = super.getRequest().getData("task", int.class);
		task = this.repository.findTaskById(taskId);
		involvedIn.setTask(task);
	}

	@Override
	public void validate(final InvolvedIn involvedIn) {
		;
	}

	@Override
	public void perform(final InvolvedIn involvedIn) {
		this.repository.save(involvedIn);
	}

	@Override
	public void unbind(final InvolvedIn involvedIn) {

		Collection<Task> tasks;
		SelectChoices choices;
		Dataset dataset;

		tasks = this.repository.findTasksPublished();
		choices = SelectChoices.from(tasks, "type", involvedIn.getTask());

		dataset = super.unbindObject(involvedIn, "task");
		dataset.put("task", choices.getSelected().getKey());
		dataset.put("tasks", choices);

		super.getResponse().addData(dataset);
	}

}
