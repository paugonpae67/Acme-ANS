
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
import acme.realms.FlightCrewMemberStatus;

@GuiService
public class FlightAssignmentUpdateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightAssignmentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		Integer Id;
		FlightAssignment assignment;
		FlightCrewMember member;
		Integer legId = super.getRequest().getData("leg", Integer.class);
		Id = super.getRequest().getData("id", Integer.class);
		Integer memberId = super.getRequest().getData("flightCrewMember", Integer.class);

		if (!super.getRequest().getMethod().equals("POST"))
			status = false;
		else if (Id == null)
			status = false;
		else if (legId == null)
			status = false;
		else if (memberId == null)
			status = false;
		else {
			Leg leg = this.repository.findLegById(legId);
			boolean validLeg = leg != null && !leg.isDraftMode(); //aqui poner mas restriccion??
			FlightCrewMember m = this.repository.findFlightCrewMemberById(memberId);
			boolean validMember = m != null && m.getAvailabilityStatus() == FlightCrewMemberStatus.AVAILABLE;

			assignment = this.repository.findAssignmentById(Id);
			member = assignment == null ? null : assignment.getFlightCrewMembers();
			status = validMember && super.getRequest().getPrincipal().hasRealm(member) && member != null && assignment.isDraftMode() && assignment != null && validLeg;

		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {

		int id;
		FlightAssignment assignment;

		id = super.getRequest().getData("id", int.class);
		assignment = this.repository.findAssignmentById(id);
		assignment.setMoment(MomentHelper.getCurrentMoment());
		super.getBuffer().addData(assignment);

	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {

		int legId;
		Leg leg;
		int memberId;
		FlightCrewMember flightCrewMember;

		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegById(legId);
		memberId = super.getRequest().getData("flightCrewMember", int.class);
		flightCrewMember = this.repository.findFlightCrewMemberById(memberId);

		super.bindObject(flightAssignment, "duty", "remarks", "currentStatus");
		flightAssignment.setLeg(leg);
		flightAssignment.setMoment(MomentHelper.getCurrentMoment());
		flightAssignment.setFlightCrewMembers(flightCrewMember);
	}

	@Override
	public void validate(final FlightAssignment FlightAssignment) {
		if (!MomentHelper.isFuture(FlightAssignment.getLeg().getScheduledDeparture()))
			super.state(false, "leg", "acme.validation.assignment.deleteleg");

	}

	@Override
	public void perform(final FlightAssignment FlightAssignment) {
		this.repository.save(FlightAssignment);

	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Collection<Leg> legs;
		SelectChoices legChoices = null;
		int memberId;

		Collection<FlightCrewMember> members;
		SelectChoices memberChoices;
		Dataset dataset;

		SelectChoices currentStatus;
		SelectChoices duty;

		legs = this.repository.findAllLegsFuturePublished(MomentHelper.getCurrentMoment());

		members = this.repository.findAllAvailableMembers();

		currentStatus = SelectChoices.from(FlightAssignmentStatus.class, assignment.getCurrentStatus());
		duty = SelectChoices.from(FlightAssignmentDuty.class, assignment.getDuty());
		memberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		Collection<Leg> legsOfMember;

		legsOfMember = this.repository.findLegsByFlightCrewMember(memberId, assignment.getId());

		for (Leg l : legsOfMember)
			if (legs.contains(l))
				legs.remove(l);

		if (!legs.contains(assignment.getLeg()))
			legs.add(assignment.getLeg());

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
