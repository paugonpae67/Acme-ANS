
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.legs.Leg;
import acme.entities.legs.LegRepository;

@Validator
public class LegValidator extends AbstractValidator<ValidLeg, Leg> {

	@Autowired
	private LegRepository		repository;

	private static final long	MINIMUM_INTERVAL_MS	= 60000; // 1 minuto en milisegundos


	@Override
	protected void initialise(final ValidLeg annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Leg leg, final ConstraintValidatorContext context) {
		assert context != null;

		if (leg == null)
			return true;

		// Validar que el flightNumber comience con el código IATA y tenga longitud 7
		if (leg.getAircraft() != null && leg.getAircraft().getAirline() != null) {
			String iataCode = leg.getAircraft().getAirline().getIataCode();
			boolean validFlightNumber = leg.getFlightNumber() != null && leg.getFlightNumber().startsWith(iataCode) && leg.getFlightNumber().length() == 7;

			super.state(context, validFlightNumber, "flightNumber", "acme.validation.leg.flightNumber.message");
		}

		// Validar que haya al menos 1 minuto entre salida y llegada
		if (leg.getScheduledDeparture() != null && leg.getScheduledArrival() != null) {
			long departure = leg.getScheduledDeparture().getTime();
			long arrival = leg.getScheduledArrival().getTime();
			boolean hasValidInterval = arrival - departure >= LegValidator.MINIMUM_INTERVAL_MS;

			super.state(context, hasValidInterval, "scheduledArrival", "acme.validation.leg.scheduledArrival.message");
		}

		// Validar que no haya solapamientos con otros segmentos
		if (leg.getFlight() != null && leg.getScheduledDeparture() != null && leg.getScheduledArrival() != null) {
			List<Leg> overlappingLegs = this.repository.findOverlappingLegs(leg.getFlight().getId(), leg.getScheduledDeparture(), leg.getScheduledArrival(), leg.getId());

			boolean noOverlap = overlappingLegs.isEmpty();
			super.state(context, noOverlap, "scheduledDeparture", "acme.validation.leg.overlapping.message");
		}

		return !super.hasErrors(context);
	}
}
