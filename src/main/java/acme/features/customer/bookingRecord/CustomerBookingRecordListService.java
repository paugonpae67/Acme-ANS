
package acme.features.customer.bookingRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Booking;
import acme.entities.bookings.BookingRecord;
import acme.realms.Customer;

@GuiService
public class CustomerBookingRecordListService extends AbstractGuiService<Customer, BookingRecord> {

	@Autowired
	private CustomerBookingRecordRepository repository;


	@Override
	public void authorise() {
		boolean status = true;

		if (!super.getRequest().getMethod().equals("GET"))
			super.getResponse().setAuthorised(false);

		else if (super.getRequest().getMethod().equals("GET") && !super.getRequest().hasData("bookingId", int.class))
			super.getResponse().setAuthorised(false);

		else if (super.getRequest().getMethod().equals("GET") && super.getRequest().hasData("id", int.class))
			super.getResponse().setAuthorised(false);

		else {
			status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
			super.getResponse().setAuthorised(status);

			int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
			Integer bookingId = super.getRequest().getData("bookingId", Integer.class);

			if (bookingId == null)
				status = false;
			else {
				Booking booking = this.repository.findBookingById(bookingId);
				if (booking == null)
					status = false;

				if (booking != null && customerId != booking.getCustomer().getId())
					status = false;
			}
			super.getResponse().setAuthorised(status);

		}
	}

	@Override
	public void load() {
		Collection<BookingRecord> bookingBookingRecords;
		int bookingId;

		bookingId = super.getRequest().getData("bookingId", int.class);
		bookingBookingRecords = this.repository.findBookingRecordByBookingId(bookingId);
		super.getBuffer().addData(bookingBookingRecords);
	}

	@Override
	public void unbind(final BookingRecord bookingRecord) {
		Dataset dataset;

		dataset = super.unbindObject(bookingRecord);
		dataset.put("passengerName", bookingRecord.getPassenger().getFullName());
		dataset.put("passengerEmail", bookingRecord.getPassenger().getEmail());
		super.addPayload(dataset, bookingRecord);
		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<BookingRecord> bookingRecords) {
		int bookingId;
		Booking booking;
		final boolean showCreate;

		bookingId = super.getRequest().getData("bookingId", int.class);
		booking = this.repository.findBookingById(bookingId);
		showCreate = booking.isDraftMode() && super.getRequest().getPrincipal().hasRealm(booking.getCustomer());

		super.getResponse().addGlobal("bookingId", bookingId);
		super.getResponse().addGlobal("showCreate", showCreate);
	}

}
