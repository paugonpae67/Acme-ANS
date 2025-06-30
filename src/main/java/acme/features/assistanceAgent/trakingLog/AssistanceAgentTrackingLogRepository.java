
package acme.features.assistanceAgent.trakingLog;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.trackingLogs.TrackingLog;

@GuiService
public interface AssistanceAgentTrackingLogRepository extends AbstractRepository {

	@Query("select t from TrackingLog t where t.claim.id = :claimId and t.id <> :trackingLogId and t.lastUpdateMoment <= :currentMoment order by t.lastUpdateMoment DESC")
	List<TrackingLog> findLatestTrackingLogByClaim(Integer claimId, Integer trackingLogId, Date currentMoment);

	@Query("select COUNT(t) from TrackingLog t where t.claim.id = :claimId and t.resolutionPercentage != 100.00 and t.id != :excludedId and t.resolutionPercentage = :resolutionPercentage")
	Long findNumberLatestTrackingLogByClaimNotFinishExceptHimself(Integer claimId, Integer excludedId, Double resolutionPercentage);

	@Query("select COUNT(t) from TrackingLog t where t.claim.id = :claimId and t.resolutionPercentage = 100.00 and t.id <> :excludedId and t.lastUpdateMoment <= :currentMoment")
	Long findNumberLatestTrackingLogByClaimFinishExceptHimself(Integer claimId, Integer excludedId, Date currentMoment);

	@Query("select t from TrackingLog t where t.claim.id = :id ORDER BY t.lastUpdateMoment DESC")
	List<TrackingLog> findTrackingLogOfClaim(int id);

	@Query("select t from TrackingLog t where t.claim.id = :id ORDER BY t.resolutionPercentage DESC")
	List<TrackingLog> findTrackingLogOfClaimOrderByPercentage(int id);

	@Query("select tr from TrackingLog tr where tr.claim.id = :masterId and tr.resolutionPercentage = 100")
	Collection<TrackingLog> findTrackingLogs100PercentageByMasterId(int masterId);

	@Query("select c from Claim c where c.id = :id")
	Claim findClaimById(int id);

	@Query("select t.claim from TrackingLog t where t.id = :id")
	Claim findClaimByTrackingLogId(int id);

	@Query("select t from TrackingLog t where t.id = :id")
	TrackingLog findTrackingLogById(int id);

	@Query("select t.claim from TrackingLog t where t.id = :id")
	Collection<Claim> findClaimByTrackingLog(int id);

	@Query("select c from Claim c where c.assistanceAgent.id = :id")
	Collection<Claim> findClaimByAssistanceAgent(int id);

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId AND t.id <> :trackingLogId ORDER BY t.lastUpdateMoment DESC")
	Optional<List<TrackingLog>> findLatestTrackingLogByClaimExceptItself(int claimId, int trackingLogId);

}
