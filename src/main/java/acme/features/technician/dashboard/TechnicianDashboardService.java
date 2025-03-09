
package acme.features.technician.dashboard;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.maintenanceRecords.MaintenanceRecord;
import acme.forms.TechnicianDashboard;
import acme.realms.Technician;

@GuiService
public class TechnicianDashboardService extends AbstractGuiService<Technician, TechnicianDashboard> {

	@Autowired
	private TechnicianDashboardRepository tdrepository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		TechnicianDashboard dashboard;
		List<Object[]> numberMaintenanceRecord;
		Optional<MaintenanceRecord> recordWithNearestInspectionDue;
		List<Aircraft> top5AircraftsWithMostTasks;
		Money averageEstimatedCostLastYear;
		Money minimumEstimatedCostLastYear;
		Money maximumEstimatedCostLastYear;
		Money sTDDEVEstimatedCostLastYear;
		Double averageEstimatedDurationTask;
		Integer minimumEstimatedDurationTask;
		Integer maximumEstimatedDurationTask;
		Double sTDDEVEstimatedDurationTask;

		Integer userAccountId = this.getRequest().getPrincipal().getAccountId();

		/*
		 * numberMaintenanceRecord = this.tdrepository.findNumberMaintenanceRecord(userAccountId);
		 * recordWithNearestInspectionDue = this.tdrepository.findRecordWithNearestInspectionDue(userAccountId);
		 * top5AircraftsWithMostTasks = this.tdrepository.findTop5AircraftsWithMostTasks(userAccountId);
		 * averageEstimatedCostLastYear= this.tdrepository.findAverageEstimatedCostLastYear(userAccountId);
		 * minimumEstimatedCostLastYear=this.tdrepository.findMinimumEstimatedCostLastYear(userAccountId);
		 * maximumEstimatedCostLastYear=this.tdrepository.findMaximumEstimatedCostLastYear(userAccountId);
		 * sTDDEVEstimatedCostLastYear=this.tdrepository.findSTDDEVEstimatedCostLastYear(userAccountId);
		 * averageEstimatedDurationTask=this.tdrepository.findAverageEstimatedDurationTask(userAccountId);
		 * minimumEstimatedDurationTask = this.tdrepository.findMinimumEstimatedDurationTask(userAccountId);
		 * maximumEstimatedDurationTask = this.tdrepository.findMaximumEstimatedDurationTask(userAccountId);
		 * sTDDEVEstimatedDurationTask = this.tdrepository.findSTDDEVEstimatedDurationTask(userAccountId);
		 */
		dashboard = new TechnicianDashboard();
		/*
		 * dashboard.setNumberMaintenanceRecord(numberMaintenanceRecord);
		 * dashboard.setRecordWithNearestInspectionDue(recordWithNearestInspectionDue);
		 * dashboard.setTop5AircraftsWithMostTasks(top5AircraftsWithMostTasks);
		 * dashboard.setAverageEstimatedCostLastYear(averageEstimatedCostLastYear);
		 * dashboard.setMinimumEstimatedCostLastYear(minimumEstimatedCostLastYear);
		 * dashboard.setMaximumEstimatedCostLastYear(maximumEstimatedCostLastYear);
		 * dashboard.setSTDDEVEstimatedCostLastYear(sTDDEVEstimatedCostLastYear);
		 * 
		 * dashboard.setAverageEstimatedDurationTask(averageEstimatedDurationTask);
		 * dashboard.setMinimumEstimatedDurationTask(minimumEstimatedDurationTask);
		 * dashboard.setMaximumEstimatedDurationTask(maximumEstimatedDurationTask);
		 * dashboard.setSTDDEVEstimatedDurationTask(sTDDEVEstimatedDurationTask);
		 */dashboard.setMaximumEstimatedDurationTask(5);
		super.getBuffer().addData(dashboard);
	}

	@Override
	public void unbind(final TechnicianDashboard object) {
		Dataset dataset;

		dataset = super.unbindObject(object, //
			"numberMaintenanceRecord", "recordWithNearestInspectionDue", // 
			"top5AircraftsWithMostTasks", "averageEstimatedCostLastYear", //
			"minimumEstimatedCostLastYear", "maximumEstimatedCostLastYear", "sTDDEVEstimatedCostLastYear", "averageEstimatedDurationTask", //
			"minimumEstimatedDurationTask", "maximumEstimatedDurationTask", "sTDDEVEstimatedDurationTask");

		super.getResponse().addData(dataset);
	}
}
