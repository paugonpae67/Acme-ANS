
package acme.features.technicians.maintenanceRecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircrafts.MaintenanceRecord;
import acme.entities.aircrafts.Task;

@Repository
public interface TechnicianMaintenanceRecordRepository extends AbstractRepository {

	@Query("select mr from MaintenanceRecord mr where mr.technician.id =:technicianId")
	Collection<MaintenanceRecord> findMaintenanceRecordByTechnicianId(int technicianId);

	@Query("select mr from MaintenanceRecord mr where mr.id =:maintenanceRecordId ")
	MaintenanceRecord findMaintenanceRecordById(int maintenanceRecordId);

	@Query("select i.task from InvolveIn i where i.maintenanceRecord.id =: maintenanceRecordId")
	Collection<Task> findTasksInvolved(int maintenanceRecordId);

}
