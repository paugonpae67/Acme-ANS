
package acme.features.technicians.task;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Task;
import acme.entities.aircrafts.Task;
import acme.features.technicians.maintenanceRecord.TechnicianMaintenanceRecordRepository;
import acme.realms.Technician;

@GuiService
public class TechnicianShowTaskService extends AbstractGuiService<Technician, Task>

	@Autowired
	private TechnicianTaskRepository repository;
	
	
	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Task task;
		Technician technician;
	
		masterId = super.getRequest().getData("id", int.class);
		task = this.repository.findTaskById(masterId);
		technician = task == null ? null : task.getTechnician();
		status = super.getRequest().getPrincipal().hasRealm(technician) || task != null;
	
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
	public void unbind(final Task task) {
		Dataset dataset;

		dataset = super.unbindObject(task, "type", "description", "priority", "estimatedDuration");
		super.getResponse().addData(dataset);
	}
}
