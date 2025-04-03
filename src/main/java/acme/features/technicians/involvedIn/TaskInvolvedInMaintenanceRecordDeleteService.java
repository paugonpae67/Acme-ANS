
package acme.features.technicians.involvedIn;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.InvolvedIn;
import acme.entities.aircrafts.MaintenanceRecord;
import acme.entities.aircrafts.Task;
import acme.realms.Technician;

@GuiService
public class TaskInvolvedInMaintenanceRecordDeleteService extends AbstractGuiService<Technician, InvolvedIn> {

	@Autowired
	private TaskInvolvedInMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int id;
		InvolvedIn involvedIn;

		id = super.getRequest().getData("id", int.class);
		involvedIn = this.repository.findInvolvedInById(id);
		status = involvedIn != null && super.getRequest().getPrincipal().hasRealm(involvedIn.getMaintenanceRecord().getTechnician());
		;

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
	public void bind(final InvolvedIn involvedIn) {
		String taskId;
		Task task;
		MaintenanceRecord maintenanceRecord;
		int masterId;
		masterId = super.getRequest().getData("masterId", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(masterId);
		taskId = super.getRequest().getData("task", String.class);
		task = this.repository.findTaskByTicker(taskId);

		super.bindObject(involvedIn);
		involvedIn.setTask(task);
		involvedIn.setMaintenanceRecord(maintenanceRecord);
	}

	@Override
	public void validate(final InvolvedIn involvedIn) {
		;
	}
	@Override
	public void perform(final InvolvedIn involvedIn) {
		this.repository.delete(involvedIn);
	}

	@Override
	public void unbind(final InvolvedIn involvedIn) {
		Dataset dataset;
		dataset = super.unbindObject(involvedIn);
		dataset.put("task", involvedIn.getTask().getDescription());

		super.getResponse().addData(dataset);
	}

}
