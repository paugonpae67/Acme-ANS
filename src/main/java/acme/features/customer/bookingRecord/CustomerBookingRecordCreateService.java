
package acme.features.customer.bookingRecord;

import java.util.Collection;
import java.util.stream.Collectors;

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
public class CustomerBookingRecordCreateService extends AbstractGuiService<Customer, BookingRecord> {

	@Autowired
	private CustomerBookingRecordRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(status);

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
		super.bindObject(bookingRecord, "passenger");
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
		SelectChoices passengerChoices;
		Collection<Passenger> bookingPassengers;
		Collection<Passenger> allPassengers;
		Collection<Passenger> customerPassengers;

		Booking booking = bookingRecord.getBooking();
		int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		bookingPassengers = this.repository.findPassengersByBookingId(booking.getId());
		allPassengers = this.repository.findAllPassengers();
		customerPassengers = this.repository.findAllCustomerPassengersByCustomerId(customerId);

		// Tus pasajeros que no están en la reserva actual
		Collection<Passenger> customerPassengersNotInBooking = customerPassengers.stream().filter(p -> !bookingPassengers.contains(p)).collect(Collectors.toList());

		// Pasajeros de otros clientes, pero publicados
		Collection<Passenger> otherPublishedPassengers = allPassengers.stream().filter(p -> !customerPassengers.contains(p) && !p.isDraftMode()).collect(Collectors.toList());

		// Pasajeros de otros clientes publicados que no están en la reserva actual
		Collection<Passenger> otherPublishedPassengersNotInBooking = otherPublishedPassengers.stream().filter(p -> !bookingPassengers.contains(p)).collect(Collectors.toList());

		// Combina ambos grupos
		customerPassengersNotInBooking.addAll(otherPublishedPassengersNotInBooking);

		// Opciones del selector
		passengerChoices = SelectChoices.from(customerPassengersNotInBooking, "fullName", bookingRecord.getPassenger());

		dataset = super.unbindObject(bookingRecord, "passenger", "booking");
		dataset.put("passengersOptions", passengerChoices);

		super.getResponse().addData(dataset);
	}

}
