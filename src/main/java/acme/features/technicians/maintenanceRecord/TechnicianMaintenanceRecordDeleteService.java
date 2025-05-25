
package acme.features.technicians.maintenanceRecord;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.InvolvedIn;
import acme.entities.aircrafts.MaintenanceRecord;
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
	}
	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		this.repository.delete(maintenanceRecord);
	}
	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {

	}

}
