
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
public class FlightAssignmentCreate extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightAssignmentClaimRepository repository;


	@Override
	public void authorise() {
		boolean statusfinal = true;

		if (!super.getRequest().getMethod().equals("GET"))
			statusfinal = false;
		else if (!super.getRequest().getMethod().equals("GET") && super.getRequest().hasData("id", int.class))
			statusfinal = false;
		else {
			Integer memberId = super.getRequest().getPrincipal().getActiveRealm().getId();

			FlightCrewMember mem = this.repository.findFlightCrewMemberById(memberId);

			boolean membertype = super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);

			statusfinal = mem != null && membertype && super.getRequest().getPrincipal().hasRealm(mem);
		}
		super.getResponse().setAuthorised(statusfinal);

	}

	@Override
	public void load() {

		FlightAssignment flightAssignment;
		FlightCrewMember member;

		member = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();
		flightAssignment = new FlightAssignment();
		flightAssignment.setDraftMode(true);
		flightAssignment.setMoment(MomentHelper.getCurrentMoment());
		flightAssignment.setFlightCrewMembers(member);
		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {
		int legId;
		Leg leg;

		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegById(legId);

		super.bindObject(flightAssignment, "duty", "remarks", "currentStatus");
		flightAssignment.setLeg(leg);
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		super.state(super.getRequest().getData("confirmation", boolean.class), "confirmation", "acme.validation.confirmation.message");
		Integer memberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		FlightCrewMember m = this.repository.findFlightCrewMemberById(memberId);
		boolean validMember = m != null && m.getAvailabilityStatus() == FlightCrewMemberStatus.AVAILABLE;
		super.state(validMember, "*", "acme.validation.assignment.statusIncorrect");

	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {

		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Collection<Leg> legs;
		SelectChoices legChoices = null;

		Dataset dataset;

		SelectChoices currentStatus;
		SelectChoices duty;

		legs = this.repository.findAllLegsFuturePublished(MomentHelper.getCurrentMoment());
		FlightCrewMember member;

		member = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();
		currentStatus = SelectChoices.from(FlightAssignmentStatus.class, assignment.getCurrentStatus());
		duty = SelectChoices.from(FlightAssignmentDuty.class, assignment.getDuty());

		dataset = super.unbindObject(assignment, "remarks", "moment", "currentStatus", "duty", "draftMode");
		dataset.put("currentStatus", currentStatus);
		legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());

		dataset.put("duty", duty);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("draftMode", true);
		dataset.put("flightCrewMembers", member.getEmployeeCode());

		super.getResponse().addData(dataset);
	}

}
