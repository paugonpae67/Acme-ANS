
package acme.features.FlightCrewMember.FlightAssignment;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightAssignment.FlightAssignment;

@Repository
public interface FlightAssignmentClaimRepository extends AbstractRepository {

	@Query("select f from FlightAssignment f where f.id = :id ")
	FlightAssignment findAssignmentById(int id);

	@Query("select f from FlightAssignment f where f.flightCrewMembers.id = :id AND f.leg.scheduledArrival > :date ")
	Collection<FlightAssignment> findFlightAssignmentInFuture(int id, Date date);

	@Query("select f from FlightAssignment f where f.flightCrewMembers.id = :id AND f.leg.scheduledArrival <= :date")
	Collection<FlightAssignment> findFlightAssignmentInPast(int id, Date date);

}
