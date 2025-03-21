
package acme.features.technicians.maintenanceRecord;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.aircrafts.MaintenanceRecord;
import acme.realms.Technician;

@GuiController
public class TechnicianMaintenanceRecordController extends AbstractGuiController<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianListMaintenanceRecordService		listService;

	@Autowired
	private TechnicianShowMaintenanceRecordService		showService;

	@Autowired
	private TechnicianCreateMaintenanceRecordService	createService;

	@Autowired
	private TechnicianUpdateMaintenanceRecordService	updateService;

	@Autowired
	private TechnicianPublishMaintenanceRecordService	publishService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addCustomCommand("publish", "update", this.publishService);
	}

}
