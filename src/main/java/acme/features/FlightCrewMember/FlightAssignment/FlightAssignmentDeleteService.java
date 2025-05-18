
package acme.features.FlightCrewMember.FlightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.flightAssignment.FlightAssignmentDuty;
import acme.entities.flightAssignment.FlightAssignmentStatus;
import acme.entities.legs.Leg;
import acme.features.FlightCrewMember.ActivityLog.ActivityLogClaimRepository;
import acme.realms.FlightCrewMember;

@GuiService
public class FlightAssignmentDeleteService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightAssignmentClaimRepository	repository;

	@Autowired
	private ActivityLogClaimRepository		activityLogRepository;


	@Override
	public void authorise() {
		boolean status;
		Integer Id;
		FlightAssignment assignment;
		FlightCrewMember member;

		Id = super.getRequest().getData("id", Integer.class);
		if (Id == null)
			status = false;
		else {
			assignment = this.repository.findAssignmentById(Id);
			member = assignment == null ? null : assignment.getFlightCrewMembers();
			status = super.getRequest().getPrincipal().hasRealm(member) && member != null && assignment.isDraftMode() && assignment != null;

		}

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		int id;
		FlightAssignment assignment;

		id = super.getRequest().getData("id", int.class);
		assignment = this.repository.findAssignmentById(id);

		super.getBuffer().addData(assignment);

	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {

		super.bindObject(flightAssignment, "remarks", "moment", "currentStatus", "duty", "leg");
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		Collection<ActivityLog> act = this.repository.getActivityLogByFlight(flightAssignment.getId());
		if (!act.isEmpty())
			super.state(false, "flightAssignment", "acme.validation.assignment.delete");

	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		Collection<ActivityLog> act = this.repository.getActivityLogByFlight(flightAssignment.getId());
		for (ActivityLog activity : act)
			this.activityLogRepository.delete(activity);
		this.repository.delete(flightAssignment);

	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		int memberId;
		Collection<Leg> legs;
		SelectChoices legChoices = null;

		Collection<FlightCrewMember> members;
		SelectChoices memberChoices;
		Dataset dataset;

		SelectChoices currentStatus;
		SelectChoices duty;

		memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		legs = this.repository.findAllLegsFuturePublished(MomentHelper.getCurrentMoment());
		Collection<Leg> legsOfMember;

		legsOfMember = this.repository.findLegsByFlightCrewMember(memberId, assignment.getId());

		for (Leg l : legsOfMember)
			if (legs.contains(l))
				legs.remove(l);

		if (!legs.contains(assignment.getLeg()))
			legs.add(assignment.getLeg());

		members = this.repository.findAllAvailableMembers();

		currentStatus = SelectChoices.from(FlightAssignmentStatus.class, assignment.getCurrentStatus());
		duty = SelectChoices.from(FlightAssignmentDuty.class, assignment.getDuty());

		memberChoices = SelectChoices.from(members, "employeeCode", assignment.getFlightCrewMembers());

		dataset = super.unbindObject(assignment, "remarks", "moment", "currentStatus", "duty", "draftMode");
		dataset.put("currentStatus", currentStatus);
		legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());

		dataset.put("duty", duty);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices == null ? "" : legChoices);
		dataset.put("flightCrewMember", memberChoices.getSelected().getKey());
		dataset.put("flightCrewMember", memberChoices);
		super.getResponse().addData(dataset);
	}

}
