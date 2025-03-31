
package acme.features.FlightCrewMember.ActivityLog;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activityLog.ActivityLog;

@Repository
public interface ActivityLogClaimRepository extends AbstractRepository {

	@Query("select f from ActivityLog f where f.flightAssignment.id = :id ")
	Collection<ActivityLog> findActivityLogByFlightAssignment(int id);

}
