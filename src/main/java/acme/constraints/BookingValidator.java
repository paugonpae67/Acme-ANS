
package acme.constraints;

import java.util.Collection;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.bookings.Booking;
import acme.entities.bookings.BookingRecordRepository;
import acme.entities.bookings.Passenger;

@Validator
public class BookingValidator extends AbstractValidator<ValidBooking, Booking> {

	@Autowired
	private BookingRecordRepository bookingRecordRepository;


	@Override
	protected void initialise(final ValidBooking annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Booking booking, final ConstraintValidatorContext context) {

		assert context != null;
		Integer bookingPassengersNumber = this.bookingRecordRepository.countPassengersByBooking(booking.getId());
		Collection<Passenger> bookingPassengers = this.bookingRecordRepository.findPassengersByBookingId(booking.getId());
		boolean passengersNotPublished = bookingPassengers.stream().anyMatch(passenger -> passenger.isDraftMode());

		if (booking.isDraftMode() == false && booking.getLastNibble() == null) {
			super.state(context, false, "LastNibble", "Un booking publicado no puede tener el campo lastNibble a null");
			return false;
		}

		if (booking.isDraftMode() == false && bookingPassengersNumber <= 0) {
			super.state(context, false, "PassengersNumber", "Un booking publicado debe tener al menos 1 pasajero");
			return false;
		}

		if (booking.isDraftMode() == false && passengersNotPublished == true) {
			super.state(context, false, "BookingPassengers", "Un booking publicado no puede tener pasajeros no publicados");
			return false;

		}
		return true;
	}

}
