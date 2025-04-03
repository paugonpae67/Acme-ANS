
package acme.features.FlightCrewMember.ActivityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.realms.FlightCrewMember;

@GuiService
public class ActivityLogShowService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private ActivityLogClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int id;
		ActivityLog activityLog;

		id = super.getRequest().getData("id", int.class);
		activityLog = this.repository.findActivityLogById(id);
		status = activityLog != null;

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
