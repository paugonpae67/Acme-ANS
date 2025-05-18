
package acme.constraints;

import java.util.Collection;
import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.flightAssignment.FlightAssignmentDuty;
import acme.entities.legs.Leg;
import acme.features.FlightCrewMember.FlightAssignment.FlightAssignmentClaimRepository;
import acme.realms.FlightCrewMemberStatus;

@Validator
public class FlightAssignmentValidator extends AbstractValidator<ValidFlightAssignemnt, FlightAssignment> {

	@Autowired
	private FlightAssignmentClaimRepository repository;


	@Override
	protected void initialise(final ValidFlightAssignemnt annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final FlightAssignment assignment, final ConstraintValidatorContext context) {

		assert context != null;
		boolean result;

		Leg leg = assignment.getLeg();

		if (assignment == null)
			super.state(context, false, "FlightCrewMembers", "acme.validation.assignment.NotNull");

		if (leg == null)
			super.state(context, false, "leg", "acme.validation.assignment.nextInspectionNotNull");
		if (assignment.getFlightCrewMembers() == null)
			super.state(context, false, "FlightCrewMembers", "acme.validation.assignment.nextInspectionNotNull");

		if (leg != null && !this.dutymember(assignment, assignment.getDuty()))
			super.state(context, false, "duty", "acme.validation.assignment.dutyIncorrect");

		if (assignment.getFlightCrewMembers() != null && !this.MemberAvaible(assignment))
			super.state(context, false, "FlightCrewMembers", "acme.validation.assignment.statusIncorrect");

		if (assignment.getFlightCrewMembers() != null && leg != null && !this.legsimultaneo(assignment, assignment.getFlightCrewMembers().getId()))
			super.state(context, false, "leg", "acme.validation.assignment.legIncorrect");

		if (leg != null && leg.isDraftMode())
			super.state(context, false, "leg", "acme.validation.assignment.legDraft");

		result = !super.hasErrors(context);

		return result;

	}
	//este funciona pero ns
	private boolean MemberAvaible(final FlightAssignment assignment) {
		FlightCrewMemberStatus status = assignment.getFlightCrewMembers().getAvailabilityStatus();
		boolean isAvaible = false;
		if (status == FlightCrewMemberStatus.AVAILABLE)
			isAvaible = true;
		else if (status != FlightCrewMemberStatus.AVAILABLE && MomentHelper.isPast(assignment.getMoment()))
			isAvaible = true;
		return isAvaible;
	}

	//este funciona
	private boolean legsimultaneo(final FlightAssignment flightAssignment, final Integer memberId) {
		Collection<Leg> legs = this.repository.findLegsByFlightCrewMember(memberId, flightAssignment.getId());
		boolean notIsSimultaneo = true;
		for (Leg l : legs) {
			Date arrive = l.getScheduledArrival();
			Date departure = l.getScheduledDeparture();
			boolean arriveComparation = MomentHelper.isInRange(flightAssignment.getLeg().getScheduledArrival(), departure, arrive);
			boolean departureComparation = MomentHelper.isInRange(flightAssignment.getLeg().getScheduledDeparture(), departure, arrive);

			if (arriveComparation == true || departureComparation == true) {

				notIsSimultaneo = false;
				break;
			}
		}
		return notIsSimultaneo;

	}

	//este funciona
	private boolean dutymember(final FlightAssignment assignment, final FlightAssignmentDuty myDuty) {
		Leg leg = assignment.getLeg();
		Collection<FlightAssignment> assignments = this.repository.findFlightAssignmentByLegId(leg.getId());
		boolean notHasDuty = true;
		FlightAssignmentDuty fDuty;

		if (myDuty != FlightAssignmentDuty.PILOT && myDuty != FlightAssignmentDuty.CO_PILOT)
			notHasDuty = true;
		else
			for (FlightAssignment f : assignments)
				if (f.getId() != assignment.getId()) {

					fDuty = f.getDuty();

					if (fDuty == FlightAssignmentDuty.PILOT && myDuty == FlightAssignmentDuty.PILOT) {
						notHasDuty = false;
						break;
					} else if (fDuty == FlightAssignmentDuty.CO_PILOT && myDuty == FlightAssignmentDuty.CO_PILOT) {
						notHasDuty = false;
						break;
					}
				}

		return notHasDuty;
	}

}
