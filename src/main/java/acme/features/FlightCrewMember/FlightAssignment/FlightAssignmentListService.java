
package acme.features.FlightCrewMember.FlightAssignment;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.FlightCrewMember;

@GuiService
public class FlightAssignmentListService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------
	/*
	 * COMPLETAR REVISAR
	 */
	@Autowired
	private FlightAssignmentClaimRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<FlightAssignment> assignments;
		Date currentDate = new Date();

		// Obtener asignaciones completadas y planificadas
		Collection<FlightAssignment> completed = this.repository.findCompletedFlightAssignments(currentDate);
		Collection<FlightAssignment> planned = this.repository.findPlannedFlightAssignments(currentDate);

		assignments = Stream.concat(completed.stream(), planned.stream()).collect(Collectors.toList());
		super.getBuffer().addData(assignments);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset dataset;

		dataset = super.unbindObject(assignment, "duty", "status", "remarks");

		// Añadir datos derivados del Leg asociado
		dataset.put("flightNumber", assignment.getLeg().getFlightNumber());
		dataset.put("scheduledDeparture", assignment.getLeg().getScheduledDeparture());
		dataset.put("scheduledArrival", assignment.getLeg().getScheduledArrival());
		dataset.put("legStatus", assignment.getLeg().getStatus());

		super.getResponse().addData(dataset);
	}

}
