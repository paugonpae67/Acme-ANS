
package acme.features.customer.bookingRecord;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Booking;
import acme.entities.bookings.BookingRecord;
import acme.entities.passengers.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerBookingRecordCreateService extends AbstractGuiService<Customer, BookingRecord> {

	@Autowired
	private CustomerBookingRecordRepository repository;


	@Override
	public void authorise() {
		int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		int bookingId = super.getRequest().getData("bookingId", int.class);
		Booking booking = this.repository.findBookingById(bookingId);

		super.getResponse().setAuthorised(customerId == booking.getCustomer().getId());
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
		super.bindObject(bookingRecord, "booking", "passenger");
	}

	@Override
	public void validate(final BookingRecord bookingRecord) {

	}

	@Override
	public void perform(final BookingRecord bookingRecord) {
		this.repository.save(bookingRecord);
	}

	@Override
	public void unbind(final BookingRecord bookingRecord) {
		Dataset dataset;

		int bookingId = super.getRequest().getData("bookingId", int.class);

		Collection<Passenger> bookingPassengers = this.repository.findPassengersByBookingId(bookingId);
		Collection<Passenger> allPassengers = this.repository.findAllPassengers();

		Collection<Passenger> passengersAvailableToAdd = new ArrayList<>();
		for (Passenger p : allPassengers)
			if (!bookingPassengers.contains(p))
				passengersAvailableToAdd.add(p);

		dataset = super.unbindObject(bookingRecord, "booking", "passenger");

		SelectChoices passengerChoices = SelectChoices.from(passengersAvailableToAdd, "fullName", bookingRecord.getPassenger());
		dataset.put("passengersOptions", passengerChoices);

		super.getResponse().addData(dataset);
	}

}
