
package acme.features.administrator.involvedIn;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.aircrafts.InvolvedIn;

@GuiController
public class AdministratorInvolvedInController extends AbstractGuiController<Administrator, InvolvedIn> {

	@Autowired
	private AdministratorInvolvedInListService		listService;

	@Autowired
	private AdministratorInvolvedInShowTaskService	showTaskService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showTaskService);
	}
}
