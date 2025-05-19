
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.BookingRecord;
import acme.entities.bookings.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerDeleteService extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	private CustomerPassengerRepository repository;


	@Override
	public void authorise() {
		try {
			boolean status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
			super.getResponse().setAuthorised(status);

			if (!super.getRequest().getMethod().equals("POST"))
				super.getResponse().setAuthorised(false);
			else {
				int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
				int passengerId = super.getRequest().getData("id", int.class);
				Passenger passenger = this.repository.findPassengerById(passengerId);

				super.getResponse().setAuthorised(customerId == passenger.getCustomer().getId() && passenger.isDraftMode());
			}
		} catch (Throwable t) {
			super.getResponse().setAuthorised(false);
		}
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Passenger passenger = this.repository.findPassengerById(id);

		super.getBuffer().addData(passenger);
	}

	@Override
	public void bind(final Passenger passenger) {
		super.bindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds");
	}

	@Override
	public void validate(final Passenger passenger) {

		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

		Collection<BookingRecord> bookingRecordAssociatedToPassenger = this.repository.findPassengerBookingRecordsByPassengerId(passenger.getId());
		if (!bookingRecordAssociatedToPassenger.isEmpty())
			super.state(false, "*", "acme.validation.passengerBookingRecords.message");

	}

	@Override
	public void perform(final Passenger passenger) {
		Collection<BookingRecord> bookingRecordRelations;

		bookingRecordRelations = this.repository.findPassengerBookingRecordsByPassengerId(passenger.getId());
		if (!bookingRecordRelations.isEmpty())
			this.repository.deleteAll(bookingRecordRelations);
		this.repository.delete(passenger);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;
		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds", "draftMode", "customer");

		super.getResponse().addData(dataset);
	}

}
