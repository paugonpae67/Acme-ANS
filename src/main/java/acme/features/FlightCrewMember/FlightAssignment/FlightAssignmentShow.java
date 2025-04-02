
package acme.features.FlightCrewMember.FlightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.flightAssignment.FlightAssignmentDuty;
import acme.entities.flightAssignment.FlightAssignmentStatus;
import acme.entities.legs.Leg;
import acme.realms.FlightCrewMember;

@GuiService
public class FlightAssignmentShow extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightAssignmentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		FlightAssignment assignment;
		FlightCrewMember member;

		masterId = super.getRequest().getData("id", int.class);
		assignment = this.repository.findAssignmentById(masterId);
		member = assignment == null ? null : assignment.getFlightCrewMembers();
		status = super.getRequest().getPrincipal().hasRealm(member) || assignment != null;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightAssignment assignment;
		int id;

		id = super.getRequest().getData("id", int.class);
		assignment = this.repository.findAssignmentById(id);

		super.getBuffer().addData(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		int memberId;
		Collection<Leg> legs;
		SelectChoices legChoices;

		Collection<FlightCrewMember> members;
		SelectChoices memberChoices;
		Dataset dataset;

		SelectChoices currentStatus;
		SelectChoices duty;

		memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		legs = this.repository.findAllLegs();
		members = this.repository.findAllAvailableMembers();

		currentStatus = SelectChoices.from(FlightAssignmentStatus.class, assignment.getCurrentStatus());
		duty = SelectChoices.from(FlightAssignmentDuty.class, assignment.getDuty());

		legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());
		memberChoices = SelectChoices.from(members, "employeeCode", assignment.getFlightCrewMembers());

		dataset = super.unbindObject(assignment, "remarks", "moment", "currentStatus", "duty", "draftMode");
		dataset.put("currentStatus", currentStatus);
		dataset.put("duty", duty);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("flightCrewMember", memberChoices.getSelected().getKey());
		dataset.put("flightCrewMember", memberChoices);
		super.getResponse().addData(dataset);

	}
}
