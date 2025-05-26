
package acme.features.technicians.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.MaintenanceRecord;
import acme.entities.aircrafts.MaintenanceStatus;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordShowService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		MaintenanceRecord maintenanceRecord;
		int technician;

		masterId = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(masterId);
		technician = super.getRequest().getPrincipal().getActiveRealm().getId();
		if (maintenanceRecord != null)
			status = technician == maintenanceRecord.getTechnician().getId() || !maintenanceRecord.isDraftMode() && super.getRequest().getPrincipal().hasRealmOfType(Technician.class);
		else
			status = false;
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
	public void unbind(final MaintenanceRecord maintenanceRecord) {

		SelectChoices choices;
		SelectChoices aircrafts;
		Collection<Aircraft> aircraftsCollection;
		Dataset dataset;
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
