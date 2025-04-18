
package acme.features.FlightCrewMember.FlightAssignment;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.FlightCrewMember;

@GuiController
public class FlightAssignmentController extends AbstractGuiController<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightAssignmentListPastService		PastlistService;

	@Autowired
	private FlightAssignmentListFutureService	FuturelistService;

	@Autowired
	private FlightAssignmentShow				showService;

	@Autowired
	private FlightAssignmentCreate				createService;

	@Autowired
	private FlightAssignmentDeleteService		deleteService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addCustomCommand("list-past", "list", this.PastlistService);
		super.addCustomCommand("list-future", "list", this.FuturelistService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("delete", this.deleteService);
	}

}
