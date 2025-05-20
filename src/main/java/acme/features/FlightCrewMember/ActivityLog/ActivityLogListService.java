
package acme.features.FlightCrewMember.ActivityLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.FlightCrewMember;

@GuiService
public class ActivityLogListService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------
	/*
	 * COMPLETAR REVISAR
	 */
	@Autowired
	private ActivityLogClaimRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		Integer masterId;
		FlightAssignment flightAssignment;

		masterId = super.getRequest().getData("masterId", Integer.class);
		flightAssignment = this.repository.findFlightAssignmentById(masterId);
		status = masterId != null && flightAssignment != null && super.getRequest().getPrincipal().hasRealm(flightAssignment.getFlightCrewMembers());

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int flightAssignemnetId;

		flightAssignemnetId = super.getRequest().getData("masterId", int.class);
		Collection<ActivityLog> assignments = this.repository.findActivityLogsByMasterId(flightAssignemnetId);
		super.getBuffer().addData(assignments);

	}

	@Override
	public void unbind(final ActivityLog assignment) {
		Dataset dataset;

		dataset = super.unbindObject(assignment, "typeOfIncident", "registrationMoment", "saverityLevel");

		super.getResponse().addData(dataset);

	}

	@Override
	public void unbind(final Collection<ActivityLog> activityLog) {
		int masterId;
		FlightAssignment flightAssignment;
		final boolean showCreate;

		masterId = super.getRequest().getData("masterId", int.class);
		flightAssignment = this.repository.findFlightAssignmentById(masterId);
		boolean inPast = MomentHelper.isPast(flightAssignment.getLeg().getScheduledArrival());
		boolean correctMember = super.getRequest().getPrincipal().getActiveRealm().getId() == flightAssignment.getFlightCrewMembers().getId();
		showCreate = !flightAssignment.isDraftMode() && inPast && correctMember;

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addGlobal("showCreate", showCreate);

	}

}
