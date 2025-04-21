
package acme.forms;

import java.util.Collection;

import acme.client.components.basis.AbstractForm;
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
	Collection<Aircraft>		top5AircraftsWithMostTasks;
	Double						averageEstimatedCostLastYear;
	Double						minimumEstimatedCostLastYear;
	Double						maximumEstimatedCostLastYear;
	Double						sTDDEVEstimatedCostLastYear;
	Double						averageEstimatedDurationTask;
	Integer						minimumEstimatedDurationTask;
	Integer						maximumEstimatedDurationTask;
	Double						sTDDEVEstimatedDurationTask;

}
