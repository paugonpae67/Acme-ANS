
package acme.features.FlightCrewMember.FlightAssignment;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.FlightCrewMember;

@GuiService
public class FlightAssignmentShow extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightAssignmentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		FlightAssignment assignment;
		FlightCrewMember member;

		masterId = super.getRequest().getData("id", int.class);
		assignment = this.repository.findAssignmentById(masterId);
		member = assignment == null ? null : assignment.getFlightCrewMembers();
		status = super.getRequest().getPrincipal().hasRealm(member) || assignment != null;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightAssignment assignment;
		int id;

		id = super.getRequest().getData("id", int.class);
		assignment = this.repository.findAssignmentById(id);

		super.getBuffer().addData(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset dataset;

		dataset = super.unbindObject(assignment, "remarks", "moment", "currentStatus", "duty");
		dataset.put("scheduledArrival", assignment.getLeg().getScheduledArrival());
		dataset.put("flightNumber", assignment.getLeg().getFlightNumber());
		dataset.put("scheduledDeparture", assignment.getLeg().getScheduledDeparture());
		dataset.put("status", assignment.getLeg().getStatus());
		dataset.put("Duration", assignment.getLeg().getDuration());
		dataset.put("employeeCode", assignment.getFlightCrewMembers().getEmployeeCode());
		dataset.put("phoneNumber", assignment.getFlightCrewMembers().getPhoneNumber());
		dataset.put("languageSkills", assignment.getFlightCrewMembers().getLanguageSkills());
		dataset.put("salary", assignment.getFlightCrewMembers().getSalary());
		dataset.put("yearsOfExperience", assignment.getFlightCrewMembers().getYearsOfExperience());
		dataset.put("availabilityStatus", assignment.getFlightCrewMembers().getAvailabilityStatus());

		super.getResponse().addData(dataset);

	}
}
