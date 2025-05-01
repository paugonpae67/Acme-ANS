
package acme.features.technicians.dashboard;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.MaintenanceRecord;
import acme.entities.aircrafts.MaintenanceStatus;

@Repository
public interface TechnicianDashboardRepository extends AbstractRepository {

	@Query("select count(mr) from MaintenanceRecord mr where mr.technician.id = :technicianId and mr.status = :estado and mr.draftMode = false ")
	Integer findNumberMaintenanceRecordStatus(int technicianId, MaintenanceStatus estado);
	/*
	 * @Query("select count(mr) from MaintenanceRecord mr where mr.technician.id = :technicianId and mr.status = acme.entities.aircrafts.MaintenanceStatus.IN_PROGRESS and mr.draftMode = False ")
	 * Integer findNumberMaintenanceRecordInProgress(int technicianId);
	 * 
	 * @Query("select count(mr) from MaintenanceRecord mr where mr.technician.id = :technicianId and mr.status = acme.entities.aircrafts.MaintenanceStatus.COMPLETED and mr.draftMode = False ")
	 * Integer findNumberMaintenanceRecordCompleted(int technicianId);
	 */
	@Query("select mr from MaintenanceRecord mr where mr.technician.id = :technicianId and mr.draftMode = false and exists (select i from InvolvedIn i where i.maintenanceRecord = mr) order by mr.nextInspection ASC ")
	Collection<MaintenanceRecord> findRecordWithNearestInspection(int technicianId);
	@Query(" select mr.aircraft from MaintenanceRecord mr join InvolvedIn i on i.maintenanceRecord = mr where mr.draftMode = false and mr.technician.id = :technicianId group by mr.aircraft order by count(i) desc")
	Collection<Aircraft> findTop5AircraftsWithMostTasks(int technicianId);

	@Query("select avg(mr.estimatedCost.amount) from MaintenanceRecord mr where mr.technician.id = ?1 and mr.draftMode = false and mr.estimatedCost.currency = 'EUR' and YEAR(mr.maintenanceMoment) = ?2")
	Double findAverageEstimatedCostLastYearEUR(int technicianId, int currentYear);

	@Query("select min(mr.estimatedCost.amount) from MaintenanceRecord mr where mr.technician.id = ?1 and mr.draftMode = false and mr.estimatedCost.currency = 'EUR' and YEAR(mr.maintenanceMoment) = ?2")
	Double findMinimumEstimatedCostLastYearEUR(int technicianId, int currentYear);

	@Query("select max(mr.estimatedCost.amount) from MaintenanceRecord mr where mr.technician.id = ?1 and mr.draftMode = false and mr.estimatedCost.currency = 'EUR' and YEAR(mr.maintenanceMoment) = ?2")
	Double findMaximumEstimatedCostLastYearEUR(int technicianId, int currentYear);

	@Query("select stddev(mr.estimatedCost.amount) from MaintenanceRecord mr where mr.technician.id = ?1 and mr.draftMode = false and mr.estimatedCost.currency = 'EUR' and YEAR(mr.maintenanceMoment) = ?2")
	Double findSTDDEVEstimatedCostLastYearEUR(int technicianId, int currentYear);

	@Query("select avg(mr.estimatedCost.amount) from MaintenanceRecord mr where mr.technician.id = ?1 and mr.draftMode = false and mr.estimatedCost.currency = 'USD' and YEAR(mr.maintenanceMoment) = ?2")
	Double findAverageEstimatedCostLastYearUSD(int technicianId, int currentYear);

	@Query("select min(mr.estimatedCost.amount) from MaintenanceRecord mr where mr.technician.id = ?1 and mr.draftMode = false and mr.estimatedCost.currency = 'USD' and YEAR(mr.maintenanceMoment) = ?2")
	Double findMinimumEstimatedCostLastYearUSD(int technicianId, int currentYear);

	@Query("select max(mr.estimatedCost.amount) from MaintenanceRecord mr where mr.technician.id = ?1 and mr.draftMode = false and mr.estimatedCost.currency = 'USD' and YEAR(mr.maintenanceMoment) = ?2")
	Double findMaximumEstimatedCostLastYearUSD(int technicianId, int currentYear);

	@Query("select stddev(mr.estimatedCost.amount) from MaintenanceRecord mr where mr.technician.id = ?1 and mr.draftMode = false and mr.estimatedCost.currency = 'USD' and YEAR(mr.maintenanceMoment) = ?2")
	Double findSTDDEVEstimatedCostLastYearUSD(int technicianId, int currentYear);

	@Query("select avg(mr.estimatedCost.amount) from MaintenanceRecord mr where mr.technician.id = ?1 and mr.draftMode = false and mr.estimatedCost.currency = 'GBP' and YEAR(mr.maintenanceMoment) = ?2")
	Double findAverageEstimatedCostLastYearGBP(int technicianId, int currentYear);

	@Query("select min(mr.estimatedCost.amount) from MaintenanceRecord mr where mr.technician.id = ?1 and mr.draftMode = false and mr.estimatedCost.currency = 'GBP' and YEAR(mr.maintenanceMoment) = ?2")
	Double findMinimumEstimatedCostLastYearGBP(int technicianId, int currentYear);

	@Query("select max(mr.estimatedCost.amount) from MaintenanceRecord mr where mr.technician.id = ?1 and mr.draftMode = false and mr.estimatedCost.currency = 'GBP' and YEAR(mr.maintenanceMoment) = ?2")
	Double findMaximumEstimatedCostLastYearGBP(int technicianId, int currentYear);

	@Query("select stddev(mr.estimatedCost.amount) from MaintenanceRecord mr where mr.technician.id = ?1 and mr.draftMode = false and mr.estimatedCost.currency = 'GBP' and YEAR(mr.maintenanceMoment) = ?2")
	Double findSTDDEVEstimatedCostLastYearGBP(int technicianId, int currentYear);

	@Query("select avg(t.estimatedDuration) from Task t where t.technician.id=:technicianId and t.draftMode = false")
	Double findAverageEstimatedDurationTask(int technicianId);
	@Query("select min(t.estimatedDuration) from Task t where t.technician.id=:technicianId and t.draftMode = false")
	Integer findMinimumEstimatedDurationTask(int technicianId);
	@Query("select max(t.estimatedDuration) from Task t where t.technician.id=:technicianId and t.draftMode = false")
	Integer findMaximumEstimatedDurationTask(int technicianId);
	@Query("select stddev(t.estimatedDuration) from Task t where t.technician.id=:technicianId and t.draftMode = false")
	Double findSTDDEVEstimatedDurationTask(int technicianId);

}
