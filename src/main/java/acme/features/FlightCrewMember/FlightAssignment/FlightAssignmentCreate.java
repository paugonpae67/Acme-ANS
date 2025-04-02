
package acme.features.FlightCrewMember.FlightAssignment;

import java.util.Collection;
import java.util.Date;

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
public class FlightAssignmentCreate extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightAssignmentClaimRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {

		FlightAssignment flightAssignment;
		//FlightCrewMember member;

		//member = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();

		flightAssignment = new FlightAssignment();
		flightAssignment.setDraftMode(true);
		//flightAssignment.setFlightCrewMembers(member); // esto a omejor hay q comentarlo ???

		super.getBuffer().addData(flightAssignment);
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

		super.bindObject(flightAssignment, "duty", "remarks", "moment", "currentStatus");
		flightAssignment.setLeg(leg);
		flightAssignment.setFlightCrewMembers(flightCrewMember);
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		int memberId;
		FlightCrewMember flightCrewMember;
		memberId = super.getRequest().getData("flightCrewMember", int.class);
		int assignmentId;
		assignmentId = super.getRequest().getData("leg", int.class);

		Collection<Leg> legs = this.repository.findLegsByFlightCrewMember(memberId);

		for (Leg leg : legs) {
			Date arrive = leg.getScheduledArrival();
			Date departure = leg.getScheduledDeparture();

			boolean arriveComparation = MomentHelper.isInRange(flightAssignment.getLeg().getScheduledArrival(), arrive, departure);
			boolean departureComparation = MomentHelper.isInRange(flightAssignment.getLeg().getScheduledDeparture(), arrive, departure);
			if (!arriveComparation || !departureComparation) {
				super.state(false, "flightCrewMember", "acme.validation.FlightAssignment.memberHasIncompatibleLegs.message");
				break;
			}
		}

		Collection<FlightAssignment> assignment = this.repository.findFlightAssignmentByLegId(assignmentId);

		for (FlightAssignment f : assignment)
			if (f.getDuty().equals(FlightAssignmentDuty.PILOT)) {
				super.state(!flightAssignment.getDuty().equals(FlightAssignmentDuty.PILOT), "duty", "acme.validation.FlightAssignment.memberHasPILOT.message");
				break;

			} else if (f.getDuty().equals(FlightAssignmentDuty.CO_PILOT)) {
				super.state(!flightAssignment.getDuty().equals(FlightAssignmentDuty.CO_PILOT), "duty", "acme.validation.FlightAssignment.memberHasCOPILOT.message");
				break;
			}
	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {

		this.repository.save(flightAssignment);
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
		legs = this.repository.findAllLegsFuture(MomentHelper.getCurrentMoment());
		members = this.repository.findAllAvailableMembers();

		currentStatus = SelectChoices.from(FlightAssignmentStatus.class, assignment.getCurrentStatus());
		duty = SelectChoices.from(FlightAssignmentDuty.class, assignment.getDuty());

		legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());
		memberChoices = SelectChoices.from(members, "employeeCode", assignment.getFlightCrewMembers());

		dataset = super.unbindObject(assignment, "remarks", "duty", "draftMode");
		dataset.put("moment", MomentHelper.getCurrentMoment());
		dataset.put("duty", duty);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("currentStatus", currentStatus);
		dataset.put("legs", legChoices);
		dataset.put("flightCrewMember", memberChoices.getSelected().getKey());
		dataset.put("flightCrewMember", memberChoices);
		super.getResponse().addData(dataset);
	}

}
