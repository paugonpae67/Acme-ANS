
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
public class FlightAsignmentUpdateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

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
		if (Id == null)
			status = false;
		else if (legId == null)
			status = false;
		else {
			Leg leg = this.repository.findLegById(legId);
			boolean validLeg = leg != null && MomentHelper.isFuture(leg.getScheduledDeparture()) && !leg.isDraftMode(); //aqui poner mas restriccion??

			assignment = this.repository.findAssignmentById(Id);
			member = assignment == null ? null : assignment.getFlightCrewMembers();
			status = super.getRequest().getPrincipal().hasRealm(member) && member != null && assignment.isDraftMode() && assignment != null && validLeg;

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
		;
	}

	@Override
	public void perform(final FlightAssignment FlightAssignment) {
		this.repository.save(FlightAssignment);

	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Collection<Leg> legs;
		SelectChoices legChoices = null;

		Collection<FlightCrewMember> members;
		SelectChoices memberChoices;
		Dataset dataset;

		SelectChoices currentStatus;
		SelectChoices duty;

		legs = this.repository.findAllLegsFuturePublished(MomentHelper.getCurrentMoment());

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
