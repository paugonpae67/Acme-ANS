
package acme.features.FlightCrewMember.FlightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.FlightCrewMember;

@GuiService
public class FlightAssignmentListFutureService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------
	/*
	 * COMPLETAR REVISAR
	 */
	@Autowired
	private FlightAssignmentClaimRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {

		boolean status;

		if (!super.getRequest().getMethod().equals("GET"))
			status = false;
		else
			status = super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int flightCrewMemberId;

		flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Collection<FlightAssignment> assignmentsFuture = this.repository.findFlightAssignmentInFuture(flightCrewMemberId, MomentHelper.getCurrentMoment());

		super.getBuffer().addData(assignmentsFuture);

	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset dataset;

		dataset = super.unbindObject(assignment, "duty", "currentStatus");

		super.addPayload(dataset, assignment, "leg.scheduledArrival");

		super.getResponse().addData(dataset);

	}

}
