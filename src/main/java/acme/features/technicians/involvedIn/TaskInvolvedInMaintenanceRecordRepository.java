
package acme.features.technicians.involvedIn;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircrafts.InvolvedIn;
import acme.entities.aircrafts.MaintenanceRecord;
import acme.entities.aircrafts.Task;

@Repository
public interface TaskInvolvedInMaintenanceRecordRepository extends AbstractRepository {

	@Query("select m from MaintenanceRecord m where m.id = :masterId")
	MaintenanceRecord findMaintenanceRecordById(int masterId);

	@Query("select t from Task t where t.id = :taskId")
	Task findTaskById(int taskId);

	@Query("select t from Task t")
	Collection<Task> findTasksDisponibles();

	@Query("select i from InvolvedIn i where i.id = :id")
	InvolvedIn findInvolvedInById(int id);

	@Query("select i from InvolvedIn i where i.maintenanceRecord.id = :masterId")
	Collection<InvolvedIn> findInvolvedInByMaintenanceRecord(int masterId);

	@Query("select i.task from InvolvedIn i where i.maintenanceRecord.id = :id")
	Collection<Task> findAllInvolvedInMaintenanceRecord(int id);
	@Query("select i.task from InvolvedIn i where i.maintenanceRecord.id = :id and i.task.id = :taskId")
	Task findInvolvedInMaintenanceRecordTask(int id, int taskId);
	@Query("select t from Task t where t.ticker = :taskTicker")
	Task findTaskByTicker(String taskTicker);
	@Query("select i from InvolvedIn i where i.task.id=:id and i.maintenanceRecord.id=:id2")
	InvolvedIn findInvolvedInByTaskIdAndMaintenanceRecordId(int id, int id2);

}
