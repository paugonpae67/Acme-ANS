
package acme.features.technicians.maintenanceRecord;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.MaintenanceRecord;
import acme.entities.aircrafts.MaintenanceStatus;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordCreateService extends AbstractGuiService<Technician, MaintenanceRecord> {

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

		technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();

		maintenanceRecord = new MaintenanceRecord();
		maintenanceRecord.setDraftMode(true);
		maintenanceRecord.setTechnician(technician);
		super.getBuffer().addData(maintenanceRecord);
	}

	@Override
	public void bind(final MaintenanceRecord maintenanceRecord) {
		Date currentMoment;
		Aircraft aircraft;
		String aircraftRegistrationNumber;

		aircraftRegistrationNumber = super.getRequest().getData("aircraft", String.class);
		aircraft = this.repository.findAircraftByRegistrationNumber(aircraftRegistrationNumber);
		currentMoment = MomentHelper.getCurrentMoment();
		super.bindObject(maintenanceRecord, "", "nextInspection", "estimatedCost", "notes");
		maintenanceRecord.setMaintenanceMoment(currentMoment);
		maintenanceRecord.setAircraft(aircraft);
		maintenanceRecord.setStatus(MaintenanceStatus.PENDING);
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
		//SelectChoices choices;
		//choices = SelectChoices.from(MaintenanceStatus.class, maintenanceRecord.getStatus());
		dataset = super.unbindObject(maintenanceRecord, "nextInspection", "estimatedCost", "notes", "draftMode");
		dataset.put("aircraft", "");
		//dataset.put("statuses", choices);

		super.getResponse().addData(dataset);
	}
}
