
package acme.features.administrator.airline;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.airlines.Airline;
import acme.entities.airports.Airport;

@Repository
public interface AdministratorAirlineRepository extends AbstractRepository {

	@Query("SELECT a FROM Airline a")
	Collection<Airline> findAllAirlines();

	@Query("SELECT a FROM Airline a WHERE a.id = :id")
	Airline findOneAirlineById(int id);

	@Query("SELECT ap FROM Airport ap")
	Collection<Airport> findAllAirports();

	@Query("SELECT ap FROM Airport ap WHERE ap.id = :id")
	Airport findOneAirportById(int id);
}
