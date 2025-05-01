
package acme.features.technicians.involvedIn;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.aircrafts.InvolvedIn;
import acme.realms.Technician;

@GuiController
public class TaskInvolvedInMaintenanceRecordController extends AbstractGuiController<Technician, InvolvedIn> {

	@Autowired
	private TaskInvolvedInMaintenanceRecordCreateService		createService;

	@Autowired
	private TaskInvolvedInMaintenanceRecordDeleteService		deleteService;

	@Autowired
	private TaskInvolvedInMaintenanceRecordListService			listService;

	@Autowired
	private TaskInvolvedInMaintenanceRecordShowService			showService;

	@Autowired
	private TaskInvolvedInMaintenanceRecordShowDeleteService	showDeleteService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("delete", this.deleteService);
		super.addCustomCommand("delete-form", "show", this.showDeleteService);
	}
}
