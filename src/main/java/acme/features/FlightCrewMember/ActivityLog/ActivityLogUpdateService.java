
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
public class ActivityLogUpdateService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ActivityLogClaimRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		Integer Id;
		ActivityLog activity;
		FlightCrewMember member;
		Id = super.getRequest().getData("id", Integer.class);
		if (Id == null)
			status = false;
		else if (!super.getRequest().getMethod().equals("POST"))
			status = false;
		else {

			activity = this.repository.findActivityLogById(Id);
			FlightAssignment assignment = activity.getFlightAssignment();
			boolean validassignment = !assignment.isDraftMode() && assignment != null && MomentHelper.isBefore(activity.getFlightAssignment().getLeg().getScheduledArrival(), activity.getRegistrationMoment());

			member = assignment == null ? null : assignment.getFlightCrewMembers();
			status = super.getRequest().getPrincipal().hasRealm(member) && member != null && activity.isDraftMode() && activity != null && validassignment;

		}

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		ActivityLog activityLog;
		int id;

		id = super.getRequest().getData("id", int.class);
		activityLog = this.repository.findActivityLogById(id);
		activityLog.setRegistrationMoment(MomentHelper.getCurrentMoment());

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void bind(final ActivityLog activityLog) {
		super.bindObject(activityLog, "typeOfIncident", "description", "saverityLevel");
		activityLog.setRegistrationMoment(MomentHelper.getCurrentMoment());
	}

	@Override
	public void validate(final ActivityLog activityLog) {
		;
	}

	@Override
	public void perform(final ActivityLog activityLog) {
		this.repository.save(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;

		dataset = super.unbindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "saverityLevel", "draftMode");
		dataset.put("flightAssignment", activityLog.getFlightAssignment().getId());

		super.getResponse().addData(dataset);
	}

}
