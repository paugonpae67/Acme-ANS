
package acme.features.FlightCrewMember.FlightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
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
		FlightAssignment FlightAssignment;
		int id;

		id = super.getRequest().getData("id", int.class);
		FlightAssignment = this.repository.findAssignmentById(id);
		boolean correctMember = FlightAssignment.getFlightCrewMembers().getId() == super.getRequest().getPrincipal().getActiveRealm().getId();
		boolean flightAssignmentPublished = !FlightAssignment.isDraftMode();

		boolean status = correctMember && flightAssignmentPublished && FlightAssignment.isDraftMode();
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightAssignment FlightAssignment;
		int id;

		id = super.getRequest().getData("id", int.class);
		FlightAssignment = this.repository.findAssignmentById(id);

		super.getBuffer().addData(FlightAssignment);
	}

	@Override
	public void bind(final FlightAssignment FlightAssignment) {
		super.bindObject(FlightAssignment, "remarks", "moment", "currentStatus", "duty", "currentStatus");
	}

	@Override
	public void validate(final FlightAssignment FlightAssignment) {
		;
	}

	@Override
	public void perform(final FlightAssignment FlightAssignment) {
		FlightAssignment.setDraftMode(false);
		this.repository.save(FlightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment FlightAssignment) {
		int memberId;
		Collection<Leg> legs;
		SelectChoices legChoices;

		Collection<FlightCrewMember> members;
		SelectChoices memberChoices;
		Dataset dataset;

		SelectChoices currentStatus;
		SelectChoices duty;

		memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		legs = this.repository.findAllLegs();
		members = this.repository.findAllAvailableMembers();

		currentStatus = SelectChoices.from(FlightAssignmentStatus.class, FlightAssignment.getCurrentStatus());
		duty = SelectChoices.from(FlightAssignmentDuty.class, FlightAssignment.getDuty());

		legChoices = SelectChoices.from(legs, "flightNumber", FlightAssignment.getLeg());
		memberChoices = SelectChoices.from(members, "employeeCode", FlightAssignment.getFlightCrewMembers());

		dataset = super.unbindObject(FlightAssignment, "remarks", "moment", "currentStatus", "duty", "draftMode");
		dataset.put("currentStatus", currentStatus);
		dataset.put("duty", duty);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("flightCrewMember", memberChoices.getSelected().getKey());
		dataset.put("flightCrewMember", memberChoices);
		super.getResponse().addData(dataset);
	}

}
