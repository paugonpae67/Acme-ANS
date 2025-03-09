
package acme.features.technician.dashboard;

import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface TechnicianDashboardRepository extends AbstractRepository {
	/*
	 * @Query("select mr.status as status, COUNT(mr) as recordCount FROM MaintenanceRecord where mr.technician.id =: technicianId mr GROUP BY mr.status")
	 * List<Object[]> findNumberMaintenanceRecord(int technicianId);
	 * 
	 * @Query("SELECT mr FROM MaintenanceRecord mr LEFT JOIN Involves i ON i.maintenanceRecord.id = mr.id WHERE mr.technician.id = :technicianId AND mr.nextInspectionDue = (SELECT MIN(mr2.nextInspectionDue) FROM MaintenanceRecord mr2 WHERE mr2.technician.id = mr.technician.id)"
	 * )
	 * Optional<MaintenanceRecord> findRecordWithNearestInspectionDue(int technicianId);
	 * 
	 * @Query("SELECT a FROM Aircraft a LEFT JOIN MaintenanceRecord mr ON mr.aircraft.id = a.id LEFT JOIN Involves i ON i.maintenanceRecord = mr.id LEFT JOIN Task t ON t.id = i.task.id WHERE t.technician.id =: technicianId GROUP BY a.id ORDER BY  COUNT(t.id) DESC LIMIT 5"
	 * )
	 * List<Aircraft> findTop5AircraftsWithMostTasks(int technicianId);
	 * 
	 * @Query("SELECT AVG(mr.estimatedCost.amount) FROM MaintenanceRecord mr WHERE mr.technician.id = :technicianId AND YEAR(mr.maintenanceMoment)= YEAR(CURDATE()) - 1 ")
	 * Money findAverageEstimatedCostLastYear(int technicianId);
	 * 
	 * @Query("SELECT MIN(mr.estimatedCost.amount) FROM MaintenanceRecord mr WHERE mr.technician.id = :technicianId AND YEAR(mr.maintenanceMoment)= YEAR(CURDATE()) - 1 ")
	 * Money findMinimumEstimatedCostLastYear(int technicianId);
	 * 
	 * @Query("SELECT MAX(mr.estimatedCost.amount) FROM MaintenanceRecord mr WHERE mr.technician.id = :technicianId AND YEAR(mr.maintenanceMoment)= YEAR(CURDATE()) - 1 ")
	 * Money findMaximumEstimatedCostLastYear(int technicianId);
	 * 
	 * @Query("SELECT STDDEV(mr.estimatedCost.amount) FROM MaintenanceRecord mr WHERE mr.technician.id = :technicianId AND YEAR(mr.maintenanceMoment)= YEAR(CURDATE()) - 1 ")
	 * Money findSTDDEVEstimatedCostLastYear(int technicianId);
	 * 
	 * @Query("SELECT AVG(mr.estimatedDuration) FROM Task t WHERE t.technician.id = :technicianId ")
	 * Double findAverageEstimatedDurationTask(int technicianId);
	 * 
	 * @Query("SELECT MIN(mr.estimatedDuration) FROM Task t WHERE t.technician.id = :technicianId ")
	 * Integer findMinimumEstimatedDurationTask(int technicianId);
	 * 
	 * @Query("SELECT MAX(mr.estimatedDuration) FROM Task t WHERE t.technician.id = :technicianId ")
	 * Integer findMaximumEstimatedDurationTask(int technicianId);
	 * 
	 * @Query("SELECT STDDEV(t.estimatedDuration) FROM Task t WHERE t.technician.id = :technicianId")
	 * Double findSTDDEVEstimatedDurationTask(int technicianId);
	 */
}
