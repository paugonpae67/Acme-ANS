
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
			super.state(context, false, "member", "acme.validation.assignment.NotNull");
		else if (leg == null)
			super.state(context, false, "member", "acme.validation.assignment.nextInspectionNotNull");
		else if (!this.dutymember(leg, assignment.getDuty()))
			super.state(context, false, "member", "acme.validation.assignment.dutyIncorrect");
		else if (!this.MemberAvaible(assignment))
			super.state(context, false, "member", "acme.validation.assignment.statusIncorrect");
		else {
			boolean correctAssignation;

			boolean correctLeg = !leg.isDraftMode() && !this.legsimultaneo(assignment, assignment.getFlightCrewMembers().getId());

			super.state(context, correctLeg, "member", "acme.validation.assignment.legCorrect");

		}
		result = !super.hasErrors(context);

		return result;

	}
	//este funciona pero ns
	private boolean MemberAvaible(final FlightAssignment assignment) {
		FlightCrewMemberStatus status = assignment.getFlightCrewMembers().getAvailabilityStatus();
		Leg leg = assignment.getLeg();
		boolean isAvaible = false;
		if (status == FlightCrewMemberStatus.AVAILABLE)
			isAvaible = true;
		else if (status != FlightCrewMemberStatus.AVAILABLE && MomentHelper.isPast(assignment.getMoment()))
			isAvaible = true;
		return isAvaible;
	}

	//este funciona
	private boolean legsimultaneo(final FlightAssignment flightAssignment, final Integer memberId) {
		Collection<Leg> legs = this.repository.findLegsByFlightCrewMember(memberId);
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

	private boolean dutymember(final Leg leg, final FlightAssignmentDuty myDuty) {
		Collection<FlightAssignment> assignments = this.repository.findFlightAssignmentByLegId(leg.getId());
		boolean notHasDuty = true;

		for (FlightAssignment f : assignments) {
			FlightAssignmentDuty fDuty = f.getDuty();
			if (leg.getId() != f.getId())
				if (fDuty.equals(FlightAssignmentDuty.PILOT) && myDuty.equals(FlightAssignmentDuty.PILOT)) {
					notHasDuty = false;
					break;
				} else if (fDuty.equals(FlightAssignmentDuty.CO_PILOT) && myDuty.equals(FlightAssignmentDuty.CO_PILOT)) {
					notHasDuty = false;
					break;
				}
		}

		return notHasDuty;
	}

}
