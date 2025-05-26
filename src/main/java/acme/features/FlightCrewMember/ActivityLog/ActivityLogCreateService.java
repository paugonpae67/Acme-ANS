
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
public class ActivityLogCreateService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ActivityLogClaimRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = true;

		if (super.getRequest().getMethod().equals("GET") && super.getRequest().hasData("id", int.class))
			status = false;
		if (super.getRequest().getMethod().equals("POST")) {
			int id = super.getRequest().getData("id", int.class);
			status = status && id == 0;
		} else if (super.getRequest().getMethod().equals("GET") && !super.getRequest().hasData("masterId", int.class))
			status = false;

		else

		{
			Integer masterId;
			FlightAssignment flightAssignment;
			masterId = super.getRequest().getData("masterId", Integer.class);

			flightAssignment = this.repository.findFlightAssignmentById(masterId);
			boolean correctAssignment = !flightAssignment.isDraftMode() && MomentHelper.isPast(flightAssignment.getLeg().getScheduledArrival());
			status = masterId != null && correctAssignment && flightAssignment != null && super.getRequest().getPrincipal().hasRealm(flightAssignment.getFlightCrewMembers());
		}
		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		ActivityLog activityLog;
		int masterId;
		FlightAssignment flightAssignment;

		masterId = super.getRequest().getData("masterId", int.class);
		flightAssignment = this.repository.findFlightAssignmentById(masterId);

		activityLog = new ActivityLog();
		activityLog.setFlightAssignment(flightAssignment);
		activityLog.setDraftMode(true);
		activityLog.setRegistrationMoment(MomentHelper.getCurrentMoment());
		super.getBuffer().addData(activityLog);

	}

	@Override
	public void bind(final ActivityLog activityLog) {

		super.bindObject(activityLog, "typeOfIncident", "description", "saverityLevel");

	}

	@Override
	public void validate(final ActivityLog activityLog) {
		super.state(super.getRequest().getData("confirmation", boolean.class), "confirmation", "acme.validation.confirmation.message");

	}

	@Override
	public void perform(final ActivityLog activityLog) {
		this.repository.save(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;

		dataset = super.unbindObject(activityLog, "typeOfIncident", "description", "saverityLevel", "draftMode");
		dataset.put("masterId", super.getRequest().getData("masterId", int.class));
		dataset.put("registrationMoment", activityLog.getRegistrationMoment());

		super.getResponse().addData(dataset);
	}

}
