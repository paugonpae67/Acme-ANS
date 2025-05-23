
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import acme.entities.legs.Leg;
import acme.entities.legs.LegRepository;

@Component
public class LegValidator implements ConstraintValidator<ValidLeg, Leg> {

	@Autowired
	private LegRepository		repository;

	private static final long	MINIMUM_INTERVAL_MS	= 60000; // 1 minuto en milisegundos


	@Override
	public boolean isValid(final Leg leg, final ConstraintValidatorContext context) {
		if (leg == null)
			return true; // No validamos si el objeto es nulo

		boolean isValid = true;

		// Validar que el flightNumber comience con el código IATA de la aerolínea del avión
		if (leg.getAircraft() != null && leg.getAircraft().getAirline() != null) {
			String airlineIataCode = leg.getAircraft().getAirline().getIataCode();
			boolean validFlightNumber = leg.getFlightNumber() != null && leg.getFlightNumber().startsWith(airlineIataCode) && leg.getFlightNumber().length() == 7;

			if (!validFlightNumber) {
				this.addConstraintViolation(context, "flightNumber", "java.validation.leg.flightNumber.message");
				isValid = false;
			}
		}

		// Validar que haya al menos 1 minuto entre la salida y la llegada
		if (leg.getScheduledDeparture() != null && leg.getScheduledArrival() != null) {
			long departure = leg.getScheduledDeparture().getTime();
			long arrival = leg.getScheduledArrival().getTime();
			long differenceInMs = arrival - departure;
			long differenceInMn = differenceInMs / 60000;

			if (differenceInMn < 1) {
				this.addConstraintViolation(context, "scheduledArrival", "java.validation.leg.scheduledArrival.message");
				isValid = false;
			}
		}

		// Validar que no haya solapamientos con otros segmentos en el mismo vuelo
		if (leg.getFlight() != null && leg.getScheduledDeparture() != null && leg.getScheduledArrival() != null) {
			List<Leg> overlappingLegs = this.repository.findOverlappingLegs(leg.getFlight().getId(), leg.getScheduledDeparture(), leg.getScheduledArrival(), leg.getId());

			if (!overlappingLegs.isEmpty()) {
				this.addConstraintViolation(context, "scheduledDeparture", "java.validation.leg.overlapping.message");
				isValid = false;
			}
		}

		return isValid;
	}

	private void addConstraintViolation(final ConstraintValidatorContext context, final String property, final String message) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message).addPropertyNode(property).addConstraintViolation();
	}
}
