
package acme.forms;

import java.util.List;

import acme.client.components.basis.AbstractForm;
import acme.client.components.datatypes.Money;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDashboard extends AbstractForm {

	private static final long	serialVersionUID	= 1L;

	List<String>				lastFiveDestinations;
	Money						moneySpentInBookingsLastYear;
	Integer						bookingsNumberBusinessClass;
	Integer						bookingsNumberEconomyClass;
	Integer						totalBookingsLast5years;
	Money						averageBookingCostLast5Years;
	Money						minimumBookingCostLast5Years;
	Money						maximumBookingCostLast5Years;
	Money						standardDeviationBookingCostLast5Years;
	Integer						totalPassengers;
	Double						averagePassengersNumber;
	Integer						minimumPassengersNumber;
	Integer						maximumPassengersNumber;
	Double						standardDeviationPassengersNumber;
}
