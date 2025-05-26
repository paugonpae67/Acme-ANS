
package acme.features.technicians.maintenanceRecord;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.InvolvedIn;
import acme.entities.aircrafts.MaintenanceRecord;
import acme.entities.aircrafts.MaintenanceStatus;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordDeleteService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status;
		String method = super.getRequest().getMethod();

		if (method.equals("GET"))
			status = false;
		else {
			int masterId;
			MaintenanceRecord maintenanceRecord;
			int technician;

			masterId = super.getRequest().getData("id", int.class);
			maintenanceRecord = this.repository.findMaintenanceRecordById(masterId);
			technician = technician = super.getRequest().getPrincipal().getActiveRealm().getId();
			status = maintenanceRecord != null && maintenanceRecord.isDraftMode() && technician == maintenanceRecord.getTechnician().getId();

			Integer aircraftId = super.getRequest().getData("aircraft", Integer.class);
			if (aircraftId == null)
				status = false;
			else if (aircraftId != 0) {
				Aircraft existingAircraft = this.repository.findAircraftById(aircraftId);
				status = status && existingAircraft != null;
			}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		MaintenanceRecord maintenanceRecord;
		int id;
		Date currentMoment;
		currentMoment = MomentHelper.getCurrentMoment();
		id = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(id);
		maintenanceRecord.setMaintenanceMoment(currentMoment);

		super.getBuffer().addData(maintenanceRecord);
	}
	@Override
	public void bind(final MaintenanceRecord maintenanceRecord) {

		Aircraft aircraft;
		aircraft = super.getRequest().getData("aircraft", Aircraft.class);
		super.bindObject(maintenanceRecord, "ticker", "status", "nextInspection", "estimatedCost", "notes");
		maintenanceRecord.setAircraft(aircraft);
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {
		Collection<InvolvedIn> relationsInvolvedIn;
		relationsInvolvedIn = this.repository.findMaintenanceRecordInvolvedIn(maintenanceRecord.getId());
		boolean valid = relationsInvolvedIn.isEmpty();
		super.state(valid, "*", "acme.validation.form.error.TaskInvolvedMR");
		valid = maintenanceRecord.getAircraft() != null;
		super.state(valid, "aircraft", "acme.validation.form.error.invalidAircraft");
	}
	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		this.repository.delete(maintenanceRecord);
	}
	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		Dataset dataset;
		SelectChoices choices;

		SelectChoices aircrafts;
		Collection<Aircraft> aircraftsCollection;
		aircraftsCollection = this.repository.findAircrafts();
		aircrafts = SelectChoices.from(aircraftsCollection, "registrationNumber", maintenanceRecord.getAircraft());

		choices = SelectChoices.from(MaintenanceStatus.class, maintenanceRecord.getStatus());
		dataset = super.unbindObject(maintenanceRecord, "ticker", "maintenanceMoment", "nextInspection", "estimatedCost", "notes", "draftMode");
		dataset.put("status", choices.getSelected().getKey());
		dataset.put("statuses", choices);

		dataset.put("aircrafts", aircrafts);
		dataset.put("aircraft", aircrafts.getSelected().getKey());
		super.getResponse().addData(dataset);
	}

}
