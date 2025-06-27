
package acme.entities.trackingLogs;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface TrackingLogRepository extends AbstractRepository {

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId ORDER BY t.lastUpdateMoment DESC")
	Optional<List<TrackingLog>> findLatestTrackingLogByClaim(Integer claimId);

	@Query("SELECT COUNT(t) FROM TrackingLog t WHERE t.claim.id = :claimId AND t.resolutionPercentage != 100.00 AND t.id != :excludedId AND t.resolutionPercentage = :resolutionPercentage")
	Long findNumberLatestTrackingLogByClaimNotFinishExceptHimself(Integer claimId, Integer excludedId, Double resolutionPercentage);

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId AND t.id != :currentId AND t.lastUpdateMoment < :lastUpdateMoment ORDER BY t.resolutionPercentage DESC")
	List<TrackingLog> findOtherTrackingLogsBeforeCurrentOrderByPercentage(Integer claimId, Integer currentId, Date lastUpdateMoment);

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId AND t.id <> :currentId AND t.lastUpdateMoment < :lastUpdateMoment ORDER BY t.lastUpdateMoment DESC")
	List<TrackingLog> findOtherTrackingLogsBeforeCurrent(Integer claimId, Integer currentId, Date lastUpdateMoment);

	@Query("SELECT t FROM TrackingLog t WHERE t.id = :trackingLogId")
	TrackingLog findTrackingLogById(Integer trackingLogId);

	@Query("SELECT COUNT(t) FROM TrackingLog t WHERE t.claim.id = :claimId AND t.id <> :excludedId AND t.lastUpdateMoment < :lastUpdateMoment AND t.resolutionPercentage = 100.00")
	Long findNumberOtherTrackingLogsFinishedBeforeCurrent(Integer claimId, Integer excludedId, Date lastUpdateMoment);

}
