
package acme.features.FlightCrewMember.ActivityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.FlightCrewMember;

@GuiService
public class ActivityLogDeleteService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ActivityLogClaimRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = true;

		if (!super.getRequest().getMethod().equals("POST"))
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

			boolean validassignment = !assignment.isDraftMode() && assignment != null && !assignment.isDraftMode() && assignment != null
				&& MomentHelper.isBefore(activity.getFlightAssignment().getLeg().getScheduledArrival(), activity.getRegistrationMoment());

			member = assignment == null ? null : assignment.getFlightCrewMembers();
			status = status && super.getRequest().getPrincipal().hasRealm(member) && member != null && activity.isDraftMode() && activity != null && validassignment;

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
	public void bind(final ActivityLog activityLog) {
		super.bindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "saverityLevel");
	}

	@Override
	public void validate(final ActivityLog activityLog) {
		;
	}

	@Override
	public void perform(final ActivityLog activityLog) {
		this.repository.delete(activityLog);
	}

}
