
package acme.features.FlightCrewMember.FlightAssignment;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightAssignment.FlightAssignment;

@Repository
public interface FlightAssignmentClaimRepository extends AbstractRepository {

	@Query("select f from FlightAssignment f where f.id = :id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("select f from FlightAssignment f where f.flightCrewMembers.id = :id")
	Collection<FlightAssignment> findFlightAssignment(int id);

	@Query("SELECT fa FROM FlightAssignment fa WHERE fa.leg.scheduledArrival < :currentDate AND fa.leg.status = 'LANDED'")
	Collection<FlightAssignment> findCompletedFlightAssignments(Date currentDate);

	@Query("SELECT fa FROM FlightAssignment fa WHERE fa.leg.scheduledDeparture > :currentDate AND fa.leg.status != 'LANDED'")
	Collection<FlightAssignment> findPlannedFlightAssignments(Date currentDate);

}
