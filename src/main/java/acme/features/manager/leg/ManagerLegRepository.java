
package acme.features.manager.leg;

import java.util.Collection;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import acme.client.repositories.AbstractRepository;
import acme.entities.legs.Leg;

@Repository
public interface ManagerLegRepository extends AbstractRepository {

	@Query("SELECT l FROM Leg l WHERE l.id = :id")
	Leg findLegById(int id);

	@Query("SELECT l FROM Leg l WHERE l.flight.manager.id = :managerId ORDER BY l.scheduledDeparture ASC")
	Collection<Leg> findLegsByManagerId(int managerId);

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :flightId ORDER BY l.scheduledDeparture ASC")
	Collection<Leg> findLegsByflightIdOrderByMoment(int flightId);

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :flightId ORDER BY l.scheduledDeparture ASC")
	Collection<Leg> findLegsByflightId(int flightId);

	@Query("SELECT l FROM Leg l WHERE l.flight.manager.id = :managerId ORDER BY l.scheduledDeparture ASC")
	Collection<Leg> findLegsByManagerIdOrderByMoment(int managerId);

	@Query("SELECT l FROM Leg l WHERE l.flightNumber = :flightNumber")
	Leg findLegByFlightNumber(String flightNumber);

	@Modifying
	@Transactional
	@Query("DELETE FROM FlightAssignment fa WHERE fa.leg.id = :legId")
	void deleteAssignmentsByLegId(int legId);

	@Modifying
	@Transactional
	@Query("DELETE FROM Claim c WHERE c.leg.id = :legId")
	void deleteClaimsByLegId(int legId);

	@Modifying
	@Transactional
	@Query("DELETE FROM TrackingLog tl WHERE tl.claim.id IN (SELECT c.id FROM Claim c WHERE c.leg.id = :legId)")
	void deleteTrackingLogsByLegId(int legId);

	@Modifying
	@Transactional
	@Query("""
		    DELETE FROM ActivityLog al
		    WHERE al.flightAssignment.id IN (
		        SELECT fa.id FROM FlightAssignment fa WHERE fa.leg.id = :legId
		    )
		""")
	void deleteActivityLogsByLegId(int legId);

}
