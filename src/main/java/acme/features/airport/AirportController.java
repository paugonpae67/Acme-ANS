
package acme.features.airport;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.airports.Airport;

@GuiController
public class AirportController extends AbstractGuiController<Administrator, Airport> {

	@Autowired
	private AirportListService		listService;

	@Autowired
	private AirportShowService		showService;

	@Autowired
	private AirportCreateService	createService;

	@Autowired
	private AirportUpdateService	updateService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
	}

}
