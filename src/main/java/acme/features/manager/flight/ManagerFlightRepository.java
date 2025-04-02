package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flights.Flight;

@Repository
public interface ManagerFlightRepository extends AbstractRepository {

    @Query("select f from Flight f where f.manager.id = :id")
    Collection<Flight> findManyByManagerId(int id);

    @Query("select f from Flight f where f.id = :id")
    Flight findOneById(int id);
}
