
package acme.features.FlightCrewMember.FlightAssignment;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;
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
		FlightCrewMember member;

		member = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();

		flightAssignment = new FlightAssignment();
		flightAssignment.setDraftMode(true);
		flightAssignment.setFlightCrewMembers(member); // esto a omejor hay q comentarlo ???

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {
		Date currentMoment;
		String flightNumberLeg; // x id o x string ???
		Leg leg;
		String employeeCodeflightCrewMember;
		FlightCrewMember flightCrewMember;

		currentMoment = MomentHelper.getCurrentMoment();
		flightNumberLeg = super.getRequest().getData("leg", String.class);
		leg = this.repository.findLegByflightNumber(flightNumberLeg);
		employeeCodeflightCrewMember = super.getRequest().getData("flightCrewMember", String.class);
		flightCrewMember = this.repository.findFlightCrewMemberByemployeeCode(employeeCodeflightCrewMember);

		super.bindObject(flightAssignment, "duty", "remarks");
		flightAssignment.setMoment(currentMoment);
		flightAssignment.setLeg(leg);
		flightAssignment.setFlightCrewMembers(flightCrewMember);
		flightAssignment.setCurrentStatus(FlightAssignmentStatus.PENDING); // status aqui o arriba con duty (= con currentMoment)
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {

		;

	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;

		dataset = super.unbindObject(flightAssignment, "duty", "remarks", "CurrentStatus", "moment", "draftMode");
		dataset.put("leg", "");
		dataset.put("flightCrewMembers", "");

		super.getResponse().addData(dataset);
	}

}
