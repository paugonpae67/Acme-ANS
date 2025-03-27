
package acme.features.assistanceAgent.claim;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.airports.Airport;
import acme.entities.claim.Claim;
import acme.entities.legs.Leg;
import acme.realms.AssistanceAgent;

@Repository
public interface AssistanceAgentClaimRepository extends AbstractRepository {

	@Query("SELECT c FROM Claim c " + "JOIN TrackingLog t ON t.claim.id = c.id " + "WHERE c.assistanceAgent.id = :id " + "AND t.status IN ('ACCEPTED', 'REJECTED')")
	List<Claim> findCompletedClaimsByAssistanceAgent(@Param("id") int id);

	@Query("select c from Claim c where c.id=:claimId")
	Claim findClaimById(int claimId);

	@Query("SELECT t.claim FROM TrackingLog t WHERE (t.status = 'PENDING')")
	Collection<Claim> findUndergoingClaimsByAssistanceAgent(int assistanceAgentId);

	@Query("SELECT l FROM Leg l WHERE l.id = :legId")
	Leg findLegByLegId(int legId);

	@Query("SELECT l FROM Leg l WHERE (l.departureAirport.id = :airportId OR l.arrivalAirport.id = :airportId)")
	Collection<Leg> findLegByAirport(int airportId);

	@Query("SELECT a.airport FROM Airline a WHERE a.id = :id")
	Airport findAirportOfAirlineByAssistanceAgentId(int id);

	@Query("SELECT a FROM AssistanceAgent a WHERE a.id = :id")
	AssistanceAgent findAssistanceAgentById(int id);
}
