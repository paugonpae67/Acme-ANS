
package acme.forms;

import java.util.List;

import acme.client.components.basis.AbstractForm;
import acme.client.components.datatypes.Money;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.MaintenanceRecord;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TechnicianDashboard extends AbstractForm {

	private static final long	serialVersionUID	= 1L;

	Integer						numberMaintenanceRecordPending;
	Integer						numberMaintenanceRecordInProgress;
	Integer						numberMaintenanceRecordCompleted;
	MaintenanceRecord			recordWithNearestInspection;
	List<Aircraft>				top5AircraftsWithMostTasks;
	Money						averageEstimatedCostLastYear;
	Money						minimumEstimatedCostLastYear;
	Money						maximumEstimatedCostLastYear;
	Money						sTDDEVEstimatedCostLastYear;
	Double						averageEstimatedDurationTask;
	Integer						minimumEstimatedDurationTask;
	Integer						maximumEstimatedDurationTask;
	Double						sTDDEVEstimatedDurationTask;

}
