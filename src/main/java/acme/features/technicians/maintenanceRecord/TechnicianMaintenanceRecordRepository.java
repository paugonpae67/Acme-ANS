
package acme.features.technicians.maintenanceRecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.InvolvedIn;
import acme.entities.aircrafts.MaintenanceRecord;

@Repository
public interface TechnicianMaintenanceRecordRepository extends AbstractRepository {

	@Query("select mr from MaintenanceRecord mr where mr.technician.id = :technicianId")
	Collection<MaintenanceRecord> findMaintenanceRecordByTechnicianId(int technicianId);

	@Query("select mr from MaintenanceRecord mr where mr.id =:maintenanceRecordId")
	MaintenanceRecord findMaintenanceRecordById(int maintenanceRecordId);

	@Query("select a from Aircraft a where a.registrationNumber = :aircraftRegistrationNumber")
	Aircraft findAircraftByRegistrationNumber(String aircraftRegistrationNumber);

	@Query("select i from InvolvedIn i where i.maintenanceRecord.id = :id")
	Collection<InvolvedIn> findMaintenanceRecordInvolvedIn(int id);

}
