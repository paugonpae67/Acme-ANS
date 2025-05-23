
package acme.features.customer.bookingRecord;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Booking;
import acme.entities.bookings.BookingRecord;
import acme.realms.Customer;

@GuiService
public class CustomerBookingRecordShowService extends AbstractGuiService<Customer, BookingRecord> {

	@Autowired
	private CustomerBookingRecordRepository repository;


	@Override
	public void authorise() {

		if (!super.getRequest().getMethod().equals("GET"))
			super.getResponse().setAuthorised(false);
		else {
			boolean status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
			super.getResponse().setAuthorised(status);

			int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
			Integer bookingRecordId = super.getRequest().getData("id", Integer.class);
			if (bookingRecordId == null)
				super.getResponse().setAuthorised(false);
			else {
				Booking booking = this.repository.findBookingByBookingRecordId(bookingRecordId);
				super.getResponse().setAuthorised(booking != null && customerId == booking.getCustomer().getId());
			}
		}

	}

	@Override
	public void load() {
		int bookingRecordId = super.getRequest().getData("id", int.class);
		BookingRecord bookingRecord = this.repository.findBookingRecordById(bookingRecordId);

		super.getBuffer().addData(bookingRecord);
	}

	@Override
	public void unbind(final BookingRecord bookingRecord) {
		Dataset dataset;

		boolean isPassengerPublished = bookingRecord.getPassenger().isDraftMode() ? false : true;
		boolean isBookingPublished = bookingRecord.getBooking().isDraftMode() ? false : true;

		dataset = super.unbindObject(bookingRecord);
		dataset.put("bookingLocatorCode", bookingRecord.getBooking().getLocatorCode());
		dataset.put("bookingId", bookingRecord.getBooking().getId());
		dataset.put("passengerId", bookingRecord.getPassenger().getId());

		dataset.put("passengerName", bookingRecord.getPassenger().getFullName());
		dataset.put("passengerEmail", bookingRecord.getPassenger().getEmail());
		dataset.put("passportNumber", bookingRecord.getPassenger().getPassportNumber());
		dataset.put("dateOfBirth", bookingRecord.getPassenger().getDateOfBirth());
		dataset.put("specialNeeds", bookingRecord.getPassenger().getSpecialNeeds());

		dataset.put("customerCreator", bookingRecord.getPassenger().getCustomer().getIdentifier());
		dataset.put("passengerPublished", isPassengerPublished);
		dataset.put("bookingPublished", isBookingPublished);

		super.getResponse().addData(dataset);
	}

}
