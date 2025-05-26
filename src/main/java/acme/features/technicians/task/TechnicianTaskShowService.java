
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
public class TechnicianTaskShowService extends AbstractGuiService<Technician, Task> {

	@Autowired
	private TechnicianTaskRepository repository;


	@Override
	public void authorise() {
		boolean status = true;
		int masterId;
		Task task;
		int technician;
		boolean status1 = true;
		if (super.getRequest().getMethod().equals("GET") && !super.getRequest().hasData("id", int.class))
			status1 = false;
		if (!super.getRequest().getMethod().equals("GET"))
			status1 = false;
		if (super.getRequest().hasData("id", int.class)) {
			masterId = super.getRequest().getData("id", int.class);
			task = this.repository.findTaskById(masterId);
			technician = super.getRequest().getPrincipal().getActiveRealm().getId();
			if (task != null)
				status = technician == task.getTechnician().getId() || !task.isDraftMode() && super.getRequest().getPrincipal().hasRealmOfType(Technician.class);
			else
				status = false;
		}
		super.getResponse().setAuthorised(status && status1);
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
	public void unbind(final Task task) {
		Dataset dataset;
		SelectChoices choices;
		choices = SelectChoices.from(TaskType.class, task.getType());
		dataset = super.unbindObject(task, "ticker", "type", "description", "priority", "estimatedDuration", "draftMode");
		dataset.put("task", choices.getSelected().getKey());
		dataset.put("tasks", choices);
		super.getResponse().addData(dataset);
	}
}
