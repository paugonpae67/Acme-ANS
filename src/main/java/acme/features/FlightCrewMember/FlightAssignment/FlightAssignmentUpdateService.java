
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
		boolean status = true;

		if (!super.getRequest().getMethod().equals("POST"))
			status = false;
		else {
			Integer Id;
			FlightAssignment assignment;
			FlightCrewMember member;
			Integer legId = super.getRequest().getData("leg", Integer.class);
			Id = super.getRequest().getData("id", Integer.class);
			Integer memberId = super.getRequest().getPrincipal().getActiveRealm().getId();

			if (Id == null)
				status = false;
			else if (legId == null)
				status = false;
			else if (memberId == null)
				status = false;
			else if (legId != 0) {
				Leg leg = this.repository.findLegById(legId);
				boolean validLeg = leg != null && !leg.isDraftMode(); //aqui poner mas restriccion??
				FlightCrewMember m = this.repository.findFlightCrewMemberById(memberId);

				boolean validMember = m != null && m.getAvailabilityStatus() == FlightCrewMemberStatus.AVAILABLE;
				boolean correctMember = true;
				String employeeCode = super.getRequest().getData("flightCrewMembers", String.class);

				FlightCrewMember member2 = this.repository.findMemberByEmployeeCode(employeeCode);
				correctMember = member2 != null && memberId == member2.getId();

				assignment = this.repository.findAssignmentById(Id);
				member = assignment == null ? null : assignment.getFlightCrewMembers();
				status = correctMember && validMember && super.getRequest().getPrincipal().hasRealm(member) && member != null && assignment.isDraftMode() && assignment != null && validLeg;
			}
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

		Integer legId;
		Leg leg;

		legId = super.getRequest().getData("leg", Integer.class);
		leg = this.repository.findLegById(legId);

		super.bindObject(flightAssignment, "duty", "remarks", "currentStatus");
		flightAssignment.setLeg(leg);
		flightAssignment.setMoment(MomentHelper.getCurrentMoment());
	}

	@Override
	public void validate(final FlightAssignment FlightAssignment) {
		if (FlightAssignment.getLeg() == null)
			super.state(false, "leg", "acme.validation.assignment.nextInspectionNotNullleg");

		else if (FlightAssignment.getLeg() != null && !MomentHelper.isFuture(FlightAssignment.getLeg().getScheduledDeparture()))
			super.state(false, "leg", "acme.validation.assignment.deleteleg");

		Integer memberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		FlightCrewMember m = this.repository.findFlightCrewMemberById(memberId);
		boolean validMember = m != null && m.getAvailabilityStatus() == FlightCrewMemberStatus.AVAILABLE;
		super.state(validMember, "*", "acme.validation.assignment.statusIncorrect");
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
		Dataset dataset;

		SelectChoices currentStatus;
		SelectChoices duty;

		legs = this.repository.findAllLegsFuturePublished(MomentHelper.getCurrentMoment());

		currentStatus = SelectChoices.from(FlightAssignmentStatus.class, assignment.getCurrentStatus());
		duty = SelectChoices.from(FlightAssignmentDuty.class, assignment.getDuty());
		memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember member;

		member = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();

		Collection<Leg> legsOfMember;
		Integer legId = super.getRequest().getData("leg", Integer.class);

		legsOfMember = this.repository.findLegsByFlightCrewMember(memberId, assignment.getId());

		for (Leg l : legsOfMember)
			if (legs.contains(l))
				legs.remove(l);
		if (assignment.getLeg() != null && !legs.contains(assignment.getLeg()))
			legs.add(assignment.getLeg());

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
