
package acme.features.technicians.involvedIn;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.InvolvedIn;
import acme.realms.Technician;

@GuiService
public class TaskInvolvedInMaintenanceRecordListService extends AbstractGuiService<Technician, InvolvedIn> {

	@Autowired
	private TaskInvolvedInMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int masterId;
		Collection<InvolvedIn> involvedIn;

		masterId = super.getRequest().getData("id", int.class);
		involvedIn = this.repository.findInvolvedInByMaintenanceRecord(masterId);

		super.getBuffer().addData(involvedIn);
	}

	@Override
	public void unbind(final InvolvedIn involvedIn) {
		Dataset dataset;

		dataset = super.unbindObject(involvedIn, "");
		dataset.put("maintenanceRecord", involvedIn.getMaintenanceRecord());
		dataset.put("task", involvedIn.getTask());
		super.addPayload(dataset, involvedIn);

		super.getResponse().addData(dataset);
	}

}
