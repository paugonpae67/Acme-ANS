
package acme.features.administrator.involvedIn;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircrafts.InvolvedIn;
import acme.entities.aircrafts.MaintenanceRecord;
import acme.entities.aircrafts.Task;

@Repository
public interface AdministratorInvolvedInRepository extends AbstractRepository {

	@Query("select i from InvolvedIn i where i.maintenanceRecord.id = :masterId")
	Collection<InvolvedIn> findInvolvedInByMaintenanceRecord(int masterId);

	@Query("select t from Task t")
	Collection<Task> findTasksDisponibles();

	@Query("select t from Task t where t.id =:taskId ")
	Task findTaskById(int taskId);

	@Query("select i from InvolvedIn i where i.id = :id")
	InvolvedIn findInvolvedInById(int id);
	@Query("select mr from MaintenanceRecord mr where mr.id = :masterId")
	MaintenanceRecord findMaintenanceRecord(int masterId);

}
