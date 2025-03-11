
package acme.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import acme.entities.legs.Leg;

public class FlightNumberValidator implements ConstraintValidator<ValidFlightNumber, Leg> {

	private static final String FLIGHT_NUMBER_PATTERN = "^[A-Z]{2}\\d{4}$";


	@Override
	public boolean isValid(final Leg leg, final ConstraintValidatorContext context) {

		String flightNumber = leg.getFlightNumber();

		if (!flightNumber.matches(FlightNumberValidator.FLIGHT_NUMBER_PATTERN))
			return false;

		if (leg.getAircraft().getAirline() != null && leg.getAircraft().getAirline().getIataCode() != null) {
			String expectedIata = leg.getAircraft().getAirline().getIataCode();
			String actualIata = flightNumber.substring(0, 2);

			if (!expectedIata.equalsIgnoreCase(actualIata))
				return false;
		}

		return true;
	}
}
