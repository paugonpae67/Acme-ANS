
package acme.entities.trackingLogs;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface TrackingLogsRepository extends AbstractRepository {

	@Query("select t.resolutionPercentage from TrackingLogs t where t.resolutionPercentage <= resolutionPorcentage")
	Double findResolutionPercentageSmaller(Double resolutionPorcentage);
}
