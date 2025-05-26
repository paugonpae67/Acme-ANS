
package acme.features.assistanceAgent.claim;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claim.Claim;
import acme.entities.legs.Leg;
import acme.entities.trackingLogs.TrackingLog;
import acme.realms.AssistanceAgent;

@Repository
public interface AssistanceAgentClaimRepository extends AbstractRepository {

	@Query("SELECT c FROM Claim c WHERE c.assistanceAgent.id = :id")
	Collection<Claim> findCompletedClaimsByAssistanceAgent(int id);

	@Query("select c from Claim c where c.id=:claimId")
	Claim findClaimById(int claimId);

	@Query("SELECT l FROM Leg l WHERE l.id = :legId")
	Leg findLegByLegId(int legId);

	@Query("SELECT a FROM AssistanceAgent a WHERE a.id = :id")
	AssistanceAgent findAssistanceAgentById(int id);

	@Query("select c.leg from Claim c where c.id = :id")
	Leg findLegByClaimId(int id);

	@Query("select t from TrackingLog t where t.claim.id = :id")
	Collection<TrackingLog> findTrackingLogsOfClaim(int id);

	@Query("SELECT l FROM Leg l")
	Collection<Leg> findAllLeg();

	@Query("select l from Leg l where l.id = :id")
	Leg findLegById(int id);

	@Query("SELECT l FROM Leg l WHERE l.draftMode = false AND l.scheduledArrival <= :registrationMoment AND l.aircraft.airline.id = :agentAirlineId")
	Collection<Leg> findAllPublishedLegs(Date registrationMoment, int agentAirlineId);

}
