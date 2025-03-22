
package acme.features.technicians.maintenanceRecord;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.MaintenanceRecord;
import acme.entities.aircrafts.MaintenanceStatus;
import acme.realms.Technician;

@GuiService
public class TechnicianCreateMaintenanceRecordService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		MaintenanceRecord maintenanceRecord;
		Technician technician;
		Date currentMoment;
		int aircraftId;
		Aircraft aircraft;

		technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();
		currentMoment = MomentHelper.getCurrentMoment();
		aircraftId = super.getRequest().getData("aircraft", int.class);
		aircraft = this.repository.findaircraftById(aircraftId);

		maintenanceRecord = new MaintenanceRecord();
		maintenanceRecord.setMaintenanceMoment(currentMoment);
		maintenanceRecord.setAircraft(aircraft);
		maintenanceRecord.setTechnician(technician);
		maintenanceRecord.setStatus(MaintenanceStatus.PENDING);
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
		SelectChoices choices;
		choices = SelectChoices.from(MaintenanceStatus.class, maintenanceRecord.getStatus());
		dataset = super.unbindObject(maintenanceRecord, "maintenanceMoment", "status", "nextInspection", //
			"estimatedCost", "notes");
		dataset.put("statuses", choices);

		super.getResponse().addData(dataset);
	}
}
