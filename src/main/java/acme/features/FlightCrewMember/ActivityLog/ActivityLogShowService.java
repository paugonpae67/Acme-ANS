
package acme.features.FlightCrewMember.ActivityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.FlightCrewMember;

@GuiService
public class ActivityLogShowService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private ActivityLogClaimRepository repository;


	@Override
	public void authorise() {
		boolean status = true;

		if (!super.getRequest().getMethod().equals("GET"))
			status = false;
		else if (super.getRequest().getMethod().equals("GET") && !super.getRequest().hasData("id", int.class))
			status = false;

		else {
			Integer Id;
			ActivityLog activity;
			FlightCrewMember member;
			Id = super.getRequest().getData("id", Integer.class);
			if (Id == null)
				status = false;
			activity = this.repository.findActivityLogById(Id);
			FlightAssignment assignment = activity.getFlightAssignment();

			boolean validassignment = !assignment.isDraftMode() && assignment != null && MomentHelper.isBefore(activity.getFlightAssignment().getLeg().getScheduledArrival(), activity.getRegistrationMoment());

			member = assignment == null ? null : assignment.getFlightCrewMembers();
			status = status && super.getRequest().getPrincipal().hasRealm(member) && member != null && activity != null && validassignment;

		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		ActivityLog activityLog;
		int id;

		id = super.getRequest().getData("id", int.class);
		activityLog = this.repository.findActivityLogById(id);

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void unbind(final ActivityLog assignment) {
		Dataset dataset;

		dataset = super.unbindObject(assignment, "typeOfIncident", "description", "saverityLevel", "registrationMoment", "draftMode");
		dataset.put("masterId", assignment.getFlightAssignment().getId());

		super.getResponse().addData(dataset);

	}

}
