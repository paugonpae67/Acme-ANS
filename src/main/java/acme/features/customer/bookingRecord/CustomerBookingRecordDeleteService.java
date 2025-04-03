
package acme.features.customer.bookingRecord;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Booking;
import acme.entities.bookings.BookingRecord;
import acme.entities.passengers.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerBookingRecordDeleteService extends AbstractGuiService<Customer, BookingRecord> {

	@Autowired
	private CustomerBookingRecordRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int bookingRecordId = super.getRequest().getData("id", int.class);
		BookingRecord bookingRecord = this.repository.findBookingRecordById(bookingRecordId);

		super.getBuffer().addData(bookingRecord);
	}

	@Override
	public void bind(final BookingRecord bookingRecord) {
		Booking booking;
		Passenger passenger;

		int bookingId = super.getRequest().getData("bookingId", int.class);

		booking = this.repository.findBookingById(bookingId);

		int passengerId = super.getRequest().getData("passengerId", int.class);
		passenger = this.repository.findPassengerById(passengerId);

		super.bindObject(bookingRecord);
		bookingRecord.setBooking(booking);
		bookingRecord.setPassenger(passenger);
	}

	@Override
	public void validate(final BookingRecord bookingRecord) {
		boolean confirmation;

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final BookingRecord bookingRecord) {
		this.repository.delete(bookingRecord);
	}

	@Override
	public void unbind(final BookingRecord bookingRecord) {
		Dataset dataset;

		boolean isPassengerPublished = bookingRecord.getPassenger().isDraftMode() ? false : true;

		dataset = super.unbindObject(bookingRecord);
		dataset.put("bookingLocatorCode", bookingRecord.getBooking().getLocatorCode());
		dataset.put("bookingId", bookingRecord.getBooking().getId());
		dataset.put("passengerId", bookingRecord.getPassenger().getId());
		dataset.put("passengerName", bookingRecord.getPassenger().getFullName());
		dataset.put("passengerEmail", bookingRecord.getPassenger().getEmail());
		dataset.put("customerCreator", bookingRecord.getPassenger().getCustomer().getIdentifier());
		dataset.put("passengerPublished", isPassengerPublished);

		super.getResponse().addData(dataset);
	}

}
