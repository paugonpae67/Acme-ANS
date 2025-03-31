
package acme.features.FlightCrewMember.ActivityLog;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;

@Repository
public interface ActivityLogClaimRepository extends AbstractRepository {

	@Query("select f from ActivityLog f where f.flightAssignment.id = :id ")
	Collection<ActivityLog> findActivityLogByFlightAssignment(int id);

	@Query("select f from FlightAssignment f where f.id = :id ")
	FlightAssignment findAssignmentById(int id);

}
