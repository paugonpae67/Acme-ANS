
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
public class FlightAssignmentPublishService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightAssignmentClaimRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {

		boolean status;
		Integer Id;
		FlightAssignment assignment;
		FlightCrewMember member;
		Integer legId = super.getRequest().getData("leg", Integer.class);
		Id = super.getRequest().getData("id", Integer.class);
		if (Id == null)
			status = false;
		else if (legId == null)
			status = false;
		else {
			Leg leg = this.repository.findLegById(legId);
			boolean validLeg = leg != null && MomentHelper.isFuture(leg.getScheduledDeparture()) && !leg.isDraftMode();

			assignment = this.repository.findAssignmentById(Id);
			member = assignment == null ? null : assignment.getFlightCrewMembers();
			status = super.getRequest().getPrincipal().hasRealm(member) && member != null && validLeg;

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

		Integer memberId;
		memberId = super.getRequest().getData("flightCrewMember", Integer.class);

		super.state(this.legsimultaneo(flightAssignment, memberId), "flightCrewMember", "acme.validation.FlightAssignment.memberHasIncompatibleLegs.message");

		super.state(this.dutymember(flightAssignment.getDuty()), "flightCrewMember", "acme.validation.FlightAssignment.memberHasIncompatibleDuty.message");

		super.state(MomentHelper.isFuture(flightAssignment.getLeg().getScheduledDeparture()), "flightCrewMember", "acme.validation.FlightAssignment.HasIncompatibleLeg");
	}

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

	private boolean dutymember(final FlightAssignmentDuty myDuty) {
		int legId = super.getRequest().getData("leg", int.class);
		Collection<FlightAssignment> assignments = this.repository.findFlightAssignmentByLegId(legId);
		boolean notHasDuty = true;

		for (FlightAssignment f : assignments) {
			FlightAssignmentDuty fDuty = f.getDuty();
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

	@Override
	public void perform(final FlightAssignment FlightAssignment) {
		FlightAssignment.setDraftMode(false);
		this.repository.save(FlightAssignment);

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
		//if (!legs.contains(assignment.getLeg()))
		//legs.add(assignment.getLeg());

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
