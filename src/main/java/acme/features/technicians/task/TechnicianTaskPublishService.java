
package acme.features.technicians.task;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Task;
import acme.entities.aircrafts.TaskType;
import acme.realms.Technician;

@GuiService
public class TechnicianTaskPublishService extends AbstractGuiService<Technician, Task> {

	@Autowired
	private TechnicianTaskRepository repository;


	@Override
	public void authorise() {
		boolean status;
		String method = super.getRequest().getMethod();
		if (method.equals("GET"))
			status = false;
		else {
			int taskId;
			Task task;
			int technician;

			taskId = super.getRequest().getData("id", int.class);
			task = this.repository.findTaskById(taskId);
			technician = super.getRequest().getPrincipal().getActiveRealm().getId();
			status = task != null && task.isDraftMode() && technician == task.getTechnician().getId();
		}
		super.getResponse().setAuthorised(status);
	}
	@Override
	public void load() {
		Task task;
		int id;

		id = super.getRequest().getData("id", int.class);
		task = this.repository.findTaskById(id);

		super.getBuffer().addData(task);
	}

	@Override
	public void bind(final Task task) {

		super.bindObject(task, "ticker", "type", "description", "priority", "estimatedDuration");
	}

	@Override
	public void validate(final Task task) {
		Task existTask = this.repository.findTaskByTicker(task.getTicker());
		boolean valid = existTask == null || existTask.getId() == task.getId();
		super.state(valid, "ticker", "acme.validation.form.error.duplicateTicker");
	}

	@Override
	public void perform(final Task task) {
		task.setDraftMode(false);
		this.repository.save(task);
	}
	@Override
	public void unbind(final Task task) {

		Dataset dataset;
		SelectChoices choices;
		choices = SelectChoices.from(TaskType.class, task.getType());
		dataset = super.unbindObject(task, "ticker", "type", "description", "priority", "estimatedDuration", "draftMode");
		dataset.put("tasks", choices);
		super.getResponse().addData(dataset);
	}

}
