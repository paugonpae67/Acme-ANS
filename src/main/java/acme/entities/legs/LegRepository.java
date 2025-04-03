
package acme.entities.legs;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import acme.client.repositories.AbstractRepository;

public interface LegRepository extends AbstractRepository {

	@Query("SELECT COUNT(l) - 1 FROM Leg l WHERE l.flight.id = :flightId")
	Integer numberOfLayovers(@Param("flightId") int flightId);

	@Query(value = "SELECT l.scheduled_departure FROM leg l WHERE l.flight_id = :flightId ORDER BY l.scheduled_departure ASC LIMIT 1", nativeQuery = true)
	Optional<Date> findFirstScheduledDeparture(@Param("flightId") int flightId);

	@Query(value = "SELECT l.scheduled_arrival FROM leg l WHERE l.flight_id = :flightId ORDER BY l.scheduled_arrival DESC LIMIT 1", nativeQuery = true)
	Optional<Date> findLastScheduledArrival(@Param("flightId") int flightId);

	@Query(value = "SELECT a.city FROM leg l JOIN airport a ON a.id = l.departure_airport_id WHERE l.flight_id = :flightId ORDER BY l.scheduled_departure ASC LIMIT 1", nativeQuery = true)
	Optional<String> findFirstOriginCity(@Param("flightId") int flightId);

	@Query(value = "SELECT a.city FROM leg l JOIN airport a ON a.id = l.arrival_airport_id WHERE l.flight_id = :flightId ORDER BY l.scheduled_arrival DESC LIMIT 1", nativeQuery = true)
	Optional<String> findLastDestinationCity(@Param("flightId") int flightId);

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :flightId " + "AND l.id <> :legId " + "AND ((l.scheduledDeparture < :scheduledArrival AND l.scheduledArrival > :scheduledDeparture))")
	List<Leg> findOverlappingLegs(Integer flightId, Date scheduledDeparture, Date scheduledArrival, Integer legId);
}
