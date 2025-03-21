
package acme.features.technicians.maintenanceRecord;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.MaintenanceRecord;
import acme.realms.Technician;

@GuiService
public class TechnicianUpdateMaintenanceRecordService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		MaintenanceRecord maintenanceRecord;
		Technician technician;

		masterId = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(masterId);
		technician = maintenanceRecord == null ? null : maintenanceRecord.getTechnician();
		status = super.getRequest().getPrincipal().hasRealm(technician) || maintenanceRecord != null;

		super.getResponse().setAuthorised(status);
	}
	@Override
	public void load() {
		MaintenanceRecord maintenanceRecord;
		int id;

		id = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(id);

		super.getBuffer().addData(maintenanceRecord);
	}
	@Override
	public void bind(final MaintenanceRecord maintenanceRecord) {

		super.bindObject(maintenanceRecord, "maintenanceMoment", "status", "nextInspection", //
			"estimatedCost", "notes");
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {
		;
	}
	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		this.repository.save(maintenanceRecord);
	}
	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {

		Dataset dataset;

		dataset = super.unbindObject(maintenanceRecord, "maintenanceMoment", "status", "nextInspection", //
			"estimatedCost", "notes");

		super.getResponse().addData(dataset);
	}

}
