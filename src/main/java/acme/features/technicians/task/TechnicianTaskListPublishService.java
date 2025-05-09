
package acme.features.technicians.task;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Task;
import acme.realms.Technician;

@GuiService
public class TechnicianTaskListPublishService extends AbstractGuiService<Technician, Task> {

	@Autowired
	private TechnicianTaskRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Task> tasks;
		tasks = this.repository.findTasksPublish();

		super.getBuffer().addData(tasks);
	}

	@Override
	public void unbind(final Task task) {
		Dataset dataset;

		dataset = super.unbindObject(task, "ticker", "type", "description", "priority", "estimatedDuration");
		super.addPayload(dataset, task);
		super.getResponse().addData(dataset);
	}

}
