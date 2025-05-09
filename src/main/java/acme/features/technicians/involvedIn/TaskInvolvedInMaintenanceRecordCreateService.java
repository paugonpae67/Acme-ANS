
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
		boolean status;
		try {
			int masterId;
			MaintenanceRecord maintenanceRecord;

			masterId = super.getRequest().getData("masterId", int.class);
			maintenanceRecord = this.repository.findMaintenanceRecordById(masterId);
			status = maintenanceRecord != null && super.getRequest().getPrincipal().hasRealm(maintenanceRecord.getTechnician());
			super.getResponse().setAuthorised(status);

			if (super.getRequest().hasData("id")) {
				Integer taskId = super.getRequest().getData("task", Integer.class);

				if (taskId == null)
					status = false;
				else if (taskId != 0) {
					Task checkedTask = this.repository.findTaskById(taskId);
					status = status && checkedTask != null;
				}
			}
		} catch (Exception e) {
			status = false;
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		InvolvedIn involvedIn;
		int masterId;
		MaintenanceRecord maintenanceRecord;

		masterId = super.getRequest().getData("masterId", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(masterId);

		involvedIn = new InvolvedIn();
		involvedIn.setTask(null);
		involvedIn.setMaintenanceRecord(maintenanceRecord);

		super.getBuffer().addData(involvedIn);
	}

	@Override
	public void bind(final InvolvedIn involvedIn) {
		int taskTicker;
		int masterId;
		Task task;
		MaintenanceRecord maintenanceRecord;

		masterId = super.getRequest().getData("masterId", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(masterId);
		taskTicker = super.getRequest().getData("task", int.class);
		task = this.repository.findTaskById(taskTicker);

		super.bindObject(involvedIn);
		involvedIn.setTask(task);
		involvedIn.setMaintenanceRecord(maintenanceRecord);
	}

	@Override
	public void validate(final InvolvedIn involvedIn) {
		boolean valid = involvedIn.getTask() != null;
		super.state(valid, "task", "acme.validation.form.error.invalidAircraft");
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
		Collection<Task> eliminateTasks = this.repository.findAllInvolvedInMaintenanceRecord(involvedIn.getMaintenanceRecord().getId());
		tasks = this.repository.findTasksDisponibles();
		tasks.removeAll(eliminateTasks);

		try {
			choices = SelectChoices.from(tasks, "description", involvedIn.getTask());
		} catch (NullPointerException e) {
			throw new IllegalArgumentException("The selected task is not available");
		}
		dataset = super.unbindObject(involvedIn);
		dataset.put("masterId", super.getRequest().getData("masterId", int.class));
		dataset.put("task", choices.getSelected().getKey());
		dataset.put("tasks", choices);

		super.getResponse().addData(dataset);
	}

}
