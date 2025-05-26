
package acme.entities.trackingLogs;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface TrackingLogRepository extends AbstractRepository {

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId ORDER BY t.lastUpdateMoment DESC")
	Optional<List<TrackingLog>> findLatestTrackingLogByClaim(Integer claimId);

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId AND t.draftMode = false AND t.id <> :currentId ORDER BY t.lastUpdateMoment DESC")
	List<TrackingLog> findOtherTrackingLogsOrderedByLastUpdateDraft(Integer claimId, Integer currentId);

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId AND t.id <> :currentId ORDER BY t.lastUpdateMoment DESC")
	List<TrackingLog> findOtherTrackingLogsOrderedByLastUpdate(Integer claimId, Integer currentId);

	@Query("SELECT t FROM TrackingLog t WHERE t.id = :trackingLogId")
	TrackingLog findTrackingLogById(Integer trackingLogId);
}
