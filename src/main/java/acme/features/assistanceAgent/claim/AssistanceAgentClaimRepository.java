
package acme.features.assistanceAgent.claim;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claim.Claim;
import acme.entities.legs.Leg;
import acme.entities.trackingLogs.TrackingLog;
import acme.realms.AssistanceAgent;

@Repository
public interface AssistanceAgentClaimRepository extends AbstractRepository {

	@Query("select c from Claim c where c.assistanceAgent.id = :id ")
	List<Claim> findUndergoingClaimsByAssistanceAgent(int id);

	@Query("select c from Claim c where c.assistanceAgent.id = :id ")
	List<Claim> findFinishClaimsByAssistanceAgent(int id);

	@Query("select c from Claim c where c.id = :claimId")
	Claim findClaimById(int claimId);

	@Query("select l from Leg l where l.id = :legId")
	Leg findLegByLegId(int legId);

	@Query("select a from AssistanceAgent a where a.id = :id")
	AssistanceAgent findAssistanceAgentById(int id);

	@Query("select c.leg from Claim c where c.id = :id")
	Leg findLegByClaimId(int id);

	@Query("select t from TrackingLog t where t.claim.id = :id")
	Collection<TrackingLog> findTrackingLogsOfClaim(int id);

	@Query("select l from Leg l")
	Collection<Leg> findAllLeg();

	@Query("select l from Leg l where l.id = :id")
	Leg findLegById(int id);

	@Query("select l from Leg l where l.draftMode = false and l.scheduledArrival <= :registrationMoment and l.aircraft.airline.id = :agentAirlineId")
	Collection<Leg> findAllPublishedLegs(Date registrationMoment, int agentAirlineId);

}
