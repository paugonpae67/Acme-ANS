
package acme.features.assistanceAgent.trakingLog;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.trackingLogs.TrackingLog;

@GuiService
public interface AssistanceAgentTrackingLogRepository extends AbstractRepository {

	@Query("select t from TrackingLog t where t.claim.id = :id ORDER BY t.lastUpdateMoment DESC")
	Collection<TrackingLog> findTrackingLogOfClaim(int id);

	@Query("select c from Claim c where c.id = :id")
	Claim findClaimById(int id);

	@Query("select t.claim from TrackingLog t where t.id = :id")
	Claim findClaimByTrackingLogId(int id);

	@Query("select t from TrackingLog t where t.id = :id")
	TrackingLog findTrackingLogById(int id);

	@Query("select t.claim from TrackingLog t where t.id = :id")
	Collection<Claim> findClaimByTrackingLog(int id);

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId ORDER BY t.lastUpdateMoment DESC")
	Optional<List<TrackingLog>> findLatestTrackingLogByClaim(Integer claimId);

	@Query("select c from Claim c where c.assistanceAgent.id = :id")
	Collection<Claim> findClaimByAssistanceAgent(int id);

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId AND t.id <> :trackingLogId ORDER BY t.lastUpdateMoment DESC")
	Collection<TrackingLog> findTrackingLogsByClaimIdExcludingOne(int claimId, int trackingLogId);

}
