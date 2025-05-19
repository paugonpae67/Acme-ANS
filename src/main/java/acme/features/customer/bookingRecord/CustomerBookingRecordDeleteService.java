
package acme.features.customer.bookingRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Booking;
import acme.entities.bookings.BookingRecord;
import acme.entities.bookings.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerBookingRecordDeleteService extends AbstractGuiService<Customer, BookingRecord> {

	@Autowired
	private CustomerBookingRecordRepository repository;


	@Override
	public void authorise() {
		try {
			boolean status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

			if (!super.getRequest().getMethod().equals("GET") && !super.getRequest().hasData("passenger"))
				super.getResponse().setAuthorised(false);
			else if (super.getRequest().getMethod().equals("GET") && super.getRequest().hasData("id", int.class))
				super.getResponse().setAuthorised(false);
			else {
				int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
				int bookingId = super.getRequest().getData("bookingId", int.class);
				Booking booking = this.repository.findBookingById(bookingId);

				status = status && booking != null && customerId == booking.getCustomer().getId() && booking.isDraftMode();
				super.getResponse().setAuthorised(status);
			}
		} catch (Throwable t) {
			super.getResponse().setAuthorised(false);
		}
	}

	@Override
	public void load() {
		BookingRecord bookingRecord = new BookingRecord();

		int bookingId = super.getRequest().getData("bookingId", int.class);
		Booking booking = this.repository.findBookingById(bookingId);
		bookingRecord.setBooking(booking);

		super.getBuffer().addData(bookingRecord);
	}

	@Override
	public void bind(final BookingRecord bookingRecord) {
		super.bindObject(bookingRecord, "passenger");
	}

	@Override
	public void validate(final BookingRecord bookingRecord) {
		boolean confirmation;

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

		boolean valid = bookingRecord.getPassenger() != null;
		super.state(valid, "passenger", "acme.validation.form.error.NotNull");

	}

	@Override
	public void perform(final BookingRecord bookingRecord) {
		BookingRecord bookingRecordToDelete = this.repository.findBookingRecordByPassengerIdAndBookingId(bookingRecord.getPassenger().getId(), bookingRecord.getBooking().getId());

		this.repository.delete(bookingRecordToDelete);
	}

	@Override
	public void unbind(final BookingRecord bookingRecord) {
		Dataset dataset;
		SelectChoices passengerChoices;
		Collection<Passenger> bookingPassengers;

		bookingPassengers = this.repository.findPassengersByBookingId(bookingRecord.getBooking().getId());

		passengerChoices = SelectChoices.from(bookingPassengers, "fullName", bookingRecord.getPassenger());

		dataset = super.unbindObject(bookingRecord, "passenger", "booking");
		dataset.put("passengersChoices", passengerChoices);

		super.getResponse().addData(dataset);
	}

}
