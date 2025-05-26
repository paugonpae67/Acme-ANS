
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

		// Asegúrate de que sea GET y que tenga el parámetro id
		boolean isGet = super.getRequest().getMethod().equals("GET");
		boolean hasId = super.getRequest().hasData("id", int.class);

		if (!isGet || !hasId)
			status = false;
		else {
			status = super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);
			if (status) {
				int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
				Integer assignmentId = super.getRequest().getData("id", Integer.class);
				if (assignmentId == null)
					status = false;
				else {
					FlightAssignment assignment = this.repository.findAssignmentById(assignmentId);
					if (assignment == null)
						status = false;
					else
						status = memberId == assignment.getFlightCrewMembers().getId() || !assignment.isDraftMode() && super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);
				}
			}
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
		dataset.put("legs", legChoices);
		dataset.put("flightCrewMembers", assignment.getFlightCrewMembers().getEmployeeCode());
		super.getResponse().addData(dataset);

	}
}
