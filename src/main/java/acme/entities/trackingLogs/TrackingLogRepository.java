
package acme.entities.trackingLogs;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface TrackingLogRepository extends AbstractRepository {

	@Query("select t from TrackingLog t where t.claim.id = :claimId order by t.lastUpdateMoment DESC")
	Optional<List<TrackingLog>> findLatestTrackingLogByClaim(Integer claimId);

	@Query("select count(t) from TrackingLog t where t.claim.id = :claimId and t.resolutionPercentage != 100.00 and t.id != :excludedId and t.resolutionPercentage = :resolutionPercentage")
	Long findNumberLatestTrackingLogByClaimNotFinishExceptHimself(Integer claimId, Integer excludedId, Double resolutionPercentage);

	@Query("select t from TrackingLog t where t.claim.id = :claimId and t.id != :currentId and t.lastUpdateMoment < :lastUpdateMoment order by t.resolutionPercentage DESC")
	List<TrackingLog> findOtherTrackingLogsBeforeCurrentOrderByPercentage(Integer claimId, Integer currentId, Date lastUpdateMoment);

	@Query("select t from TrackingLog t where t.claim.id = :claimId and t.id <> :currentId and t.lastUpdateMoment < :lastUpdateMoment order by t.lastUpdateMoment DESC")
	List<TrackingLog> findOtherTrackingLogsBeforeCurrent(Integer claimId, Integer currentId, Date lastUpdateMoment);

	@Query("select t from TrackingLog t where t.id = :trackingLogId")
	TrackingLog findTrackingLogById(Integer trackingLogId);

	@Query("select count(t) from TrackingLog t where t.claim.id = :claimId and t.id <> :excludedId and t.lastUpdateMoment < :lastUpdateMoment and t.resolutionPercentage = 100.00")
	Long findNumberOtherTrackingLogsFinishedBeforeCurrent(Integer claimId, Integer excludedId, Date lastUpdateMoment);

}
