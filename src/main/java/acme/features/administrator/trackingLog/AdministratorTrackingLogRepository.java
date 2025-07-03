
package acme.features.administrator.trackingLog;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claim.Claim;
import acme.entities.trackingLogs.TrackingLog;

@Repository
public interface AdministratorTrackingLogRepository extends AbstractRepository {

	@Query("select t from TrackingLog t where t.claim.id = :id ORDER BY t.resolutionPercentage desc")
	List<TrackingLog> findTrackingLogOfClaimOrderByPercentage(int id);

	@Query("select c from Claim c where c.id = :id")
	Claim findClaimById(int id);

}
