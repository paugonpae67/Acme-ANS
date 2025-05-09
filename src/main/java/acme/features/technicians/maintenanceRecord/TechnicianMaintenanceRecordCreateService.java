
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
import acme.entities.aircrafts.MaintenanceRecord;
import acme.entities.aircrafts.MaintenanceStatus;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordCreateService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status = false;

		try {
			status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class);
			super.getResponse().setAuthorised(status);

			if (super.getRequest().hasData("id")) {
				Integer aircraftId = super.getRequest().getData("aircraft", Integer.class);
				if (aircraftId == null)
					status = false;
				else if (aircraftId != 0) {
					Aircraft existingAircraft = this.repository.findAircraftById(aircraftId);
					status = status && existingAircraft != null;
				}
			}
		} catch (Exception e) {
			status = false;
		}

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		MaintenanceRecord maintenanceRecord;
		Technician technician;
		Date currentMoment;
		technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();
		currentMoment = MomentHelper.getCurrentMoment();

		maintenanceRecord = new MaintenanceRecord();
		maintenanceRecord.setMaintenanceMoment(currentMoment);
		maintenanceRecord.setDraftMode(true);
		maintenanceRecord.setTechnician(technician);
		maintenanceRecord.setStatus(MaintenanceStatus.PENDING);
		super.getBuffer().addData(maintenanceRecord);
	}

	@Override
	public void bind(final MaintenanceRecord maintenanceRecord) {

		Aircraft aircraft;
		aircraft = super.getRequest().getData("aircraft", Aircraft.class);
		super.bindObject(maintenanceRecord, "ticker", "nextInspection", "estimatedCost", "notes");
		maintenanceRecord.setAircraft(aircraft);
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {
		MaintenanceRecord existMaintenanceRecord = this.repository.findMaintenanceRecordByTicker(maintenanceRecord.getTicker());
		boolean valid = existMaintenanceRecord == null || existMaintenanceRecord.getId() == maintenanceRecord.getId();
		super.state(valid, "ticker", "acme.validation.form.error.duplicateTicker");

		if (maintenanceRecord.getEstimatedCost() != null) {
			boolean validCurrency = maintenanceRecord.getEstimatedCost().getCurrency().equals("EUR") || maintenanceRecord.getEstimatedCost().getCurrency().equals("USD") || maintenanceRecord.getEstimatedCost().getCurrency().equals("GBP");
			super.state(validCurrency, "estimatedCost", "acme.validation.validCurrency");
		}

		valid = maintenanceRecord.getAircraft() != null;
		super.state(valid, "aircraft", "acme.validation.form.error.invalidAircraft");

	}
	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		this.repository.save(maintenanceRecord);
	}
	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		maintenanceRecord.setMaintenanceMoment(MomentHelper.getCurrentMoment());
		Dataset dataset;
		SelectChoices choices;
		choices = SelectChoices.from(MaintenanceStatus.class, maintenanceRecord.getStatus());
		SelectChoices aircrafts;
		Collection<Aircraft> aircraftsCollection;
		aircraftsCollection = this.repository.findAircrafts();
		try {
			aircrafts = SelectChoices.from(aircraftsCollection, "registrationNumber", maintenanceRecord.getAircraft());
		} catch (NullPointerException e) {
			throw new IllegalArgumentException("The selected aircraft is not available");
		}

		dataset = super.unbindObject(maintenanceRecord, "ticker", "maintenanceMoment", "status", "nextInspection", "estimatedCost", "notes", "draftMode");
		dataset.put("status", choices.getSelected().getKey());
		dataset.put("statuses", choices);
		dataset.put("aircrafts", aircrafts);
		dataset.put("aircraft", aircrafts.getSelected().getKey());

		super.getResponse().addData(dataset);
	}
}
