
package acme.features.administrator.aircraft;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircrafts.Aircraft;
import acme.entities.airlines.Airline;
import acme.entities.legs.Leg;

@Repository
public interface AdministratorAircraftRepository extends AbstractRepository {

	@Query("SELECT a FROM Aircraft a WHERE a.id = :id")
	Aircraft findAircraftById(int id);

	@Query("SELECT a FROM Aircraft a")
	Collection<Aircraft> findAllAircrafts();

	@Query("SELECT airline FROM Airline airline")
	Collection<Airline> findAllAirlines();

	@Query("SELECT l FROM Leg l WHERE l.aircraft.id = :aircraftId AND (l.scheduledDeparture >= CURRENT_TIMESTAMP OR (l.scheduledDeparture <= CURRENT_TIMESTAMP AND l.scheduledArrival >= CURRENT_TIMESTAMP))")
	Collection<Leg> findActiveAndUpcomingLegsByAircraftId(int aircraftId);

}
