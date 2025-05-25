
package acme.features.FlightCrewMember.FlightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
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
		Integer Id;
		FlightAssignment assignment;
		FlightCrewMember member;

		Id = super.getRequest().getData("id", Integer.class);
		if (!super.getRequest().getMethod().equals("GET"))
			status = false;
		else if (super.getRequest().getMethod().equals("GET") && !super.getRequest().hasData("id", int.class))
			status = false;
		else if (Id == null)
			status = false;
		else {
			assignment = this.repository.findAssignmentById(Id);
			member = assignment == null ? null : assignment.getFlightCrewMembers();
			status = (super.getRequest().getPrincipal().hasRealm(member) || !assignment.isDraftMode()) && assignment != null;

		}

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
		Collection<Leg> legsOfMember;
		SelectChoices legChoices = null;

		Dataset dataset;

		SelectChoices currentStatus;
		SelectChoices duty;

		memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		legs = this.repository.findAllLegsFuturePublished(MomentHelper.getCurrentMoment());
		legsOfMember = this.repository.findLegsByFlightCrewMember(memberId, assignment.getId());
		FlightCrewMember member;

		member = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();

		for (Leg l : legsOfMember)
			if (legs.contains(l))
				legs.remove(l);

		if (!legs.contains(assignment.getLeg()))
			legs.add(assignment.getLeg());

		currentStatus = SelectChoices.from(FlightAssignmentStatus.class, assignment.getCurrentStatus());
		duty = SelectChoices.from(FlightAssignmentDuty.class, assignment.getDuty());

		dataset = super.unbindObject(assignment, "remarks", "moment", "currentStatus", "duty", "draftMode");
		dataset.put("currentStatus", currentStatus);
		legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());

		dataset.put("duty", duty);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices == null ? "" : legChoices);
		dataset.put("flightCrewMembers", assignment.getFlightCrewMembers().getEmployeeCode());
		super.getResponse().addData(dataset);

	}
}
