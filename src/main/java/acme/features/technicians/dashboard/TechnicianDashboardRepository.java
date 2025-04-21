
package acme.features.technicians.dashboard;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.MaintenanceRecord;

@Repository
public interface TechnicianDashboardRepository extends AbstractRepository {

	@Query("select count(mr) from MaintenanceRecord mr where mr.technician.id = :technicianId and mr.status = acme.entities.aircrafts.MaintenanceStatus.PENDING ")
	Integer findNumberMaintenanceRecordPending(int technicianId);
	@Query("select count(mr) from MaintenanceRecord mr where mr.technician.id = :technicianId and mr.status = acme.entities.aircrafts.MaintenanceStatus.IN_PROGRESS ")
	Integer findNumberMaintenanceRecordInProgress(int technicianId);
	@Query("select count(mr) from MaintenanceRecord mr where mr.technician.id = :technicianId and mr.status = acme.entities.aircrafts.MaintenanceStatus.COMPLETED ")
	Integer findNumberMaintenanceRecordCompleted(int technicianId);

	@Query("select mr from MaintenanceRecord mr where mr.technician.id = :technicianId and exists (select i from InvolvedIn i where i.maintenanceRecord = mr) order by mr.nextInspection ASC ")
	Collection<MaintenanceRecord> findRecordWithNearestInspection(int technicianId);
	@Query(" select mr.aircraft from MaintenanceRecord mr join InvolvedIn i on i.maintenanceRecord = mr where mr.technician.id = :technicianId group by mr.aircraft order by count(i) desc")
	Collection<Aircraft> findTop5AircraftsWithMostTasks(int technicianId);

	@Query("select avg(mr.estimatedCost.amount) from MaintenanceRecord mr where mr.technician.id=:technicianId and YEAR(mr.maintenanceMoment) = :currentYear")
	Double findAverageEstimatedCostLastYear(int technicianId, int currentYear);
	@Query("select min(mr.estimatedCost.amount) from MaintenanceRecord mr where mr.technician.id=:technicianId and YEAR(mr.maintenanceMoment) = :currentYear")
	Double findMinimumEstimatedCostLastYear(int technicianId, int currentYear);
	@Query("select max(mr.estimatedCost.amount) from MaintenanceRecord mr where mr.technician.id=:technicianId and YEAR(mr.maintenanceMoment) = :currentYear")
	Double findMaximumEstimatedCostLastYear(int technicianId, int currentYear);
	@Query("select stddev(mr.estimatedCost.amount) from MaintenanceRecord mr where mr.technician.id=:technicianId and YEAR(mr.maintenanceMoment) = :currentYear")
	Double findSTDDEVEstimatedCostLastYear(int technicianId, int currentYear);

	@Query("select avg(t.estimatedDuration) from Task t where t.technician.id=:technicianId")
	Double findAverageEstimatedDurationTask(int technicianId);
	@Query("select min(t.estimatedDuration) from Task t where t.technician.id=:technicianId")
	Integer findMinimumEstimatedDurationTask(int technicianId);
	@Query("select max(t.estimatedDuration) from Task t where t.technician.id=:technicianId")
	Integer findMaximumEstimatedDurationTask(int technicianId);
	@Query("select stddev(t.estimatedDuration) from Task t where t.technician.id=:technicianId")
	Double findSTDDEVEstimatedDurationTask(int technicianId);

}
