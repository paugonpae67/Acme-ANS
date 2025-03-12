
package acme.forms;

import java.util.List;

import acme.client.components.basis.AbstractForm;
import acme.client.components.datatypes.Money;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagerDashboard extends AbstractForm {

	private static final long	serialVersionUID	= 1L;

	Integer						rankingPosition;
	Integer						yearsToRetirement;
	Double						onTimeToDelayedRatio;

	List<String>				mostPopularAirports;
	List<String>				leastPopularAirports;

	Integer						legsOnTime;
	Integer						legsDelayed;
	Integer						legsCancelled;
	Integer						legsLanded;

	Money						averageFlightCost;
	Money						minimumFlightCost;
	Money						maximumFlightCost;
	Money						standardDeviationFlightCost;
}
