
package acme.features.technicians.task;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.aircrafts.Task;
import acme.realms.Technician;

@GuiController
public class TechnicianTaskController extends AbstractGuiController<Technician, Task> {

	@Autowired
	private TechnicianListTaskService		listService;

	@Autowired
	private TechnicianShowTaskService		showService;

	@Autowired
	private TechnicianCreateTaskService		createService;

	@Autowired
	private TechnicianUpdateTaskService		updateService;

	@Autowired
	private TechnicianPublishTaskService	publishService;

	@Autowired
	private TechnicianDeleteTaskService		deleteService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);
		super.addCustomCommand("publish", "update", this.publishService);
	}

}
