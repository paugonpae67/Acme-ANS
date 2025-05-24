
package acme.features.customer.booking;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Booking;
import acme.entities.bookings.TravelClass;
import acme.entities.flights.Flight;
import acme.realms.Customer;

@GuiService
public class CustomerBookingUpdateService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		boolean status = true;

		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(status);

		if (!super.getRequest().getMethod().equals("POST"))
			super.getResponse().setAuthorised(false);

		else {
			int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
			Integer bookingId = super.getRequest().getData("id", Integer.class);

			if (bookingId == null)
				status = false;
			else {
				Booking booking = this.repository.findBookingById(bookingId);
				if (booking == null || !booking.isDraftMode())
					status = false;

				Integer flightId = super.getRequest().getData("flight", Integer.class);
				if (flightId == null)
					status = false;
				else if (flightId != 0) {
					Flight flight = this.repository.findFlightById(flightId);
					status = status && flight != null && !flight.isDraftMode() && flight.getScheduledDeparture() != null && flight.getScheduledDeparture().after(MomentHelper.getCurrentMoment());
				}
				status = status && customerId == booking.getCustomer().getId();
			}
			super.getResponse().setAuthorised(status);
		}
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Booking booking = this.repository.findBookingById(id);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		super.bindObject(booking, "locatorCode", "travelClass", "price", "lastNibble", "flight");
	}

	@Override
	public void validate(final Booking booking) {
		Booking bookingAlreadyExists = this.repository.findBookingByLocatorCode(booking.getLocatorCode());
		boolean locatorIsNotValid = bookingAlreadyExists == null || bookingAlreadyExists.getId() == booking.getId();
		super.state(locatorIsNotValid, "locatorCode", "customer.booking.form.error.duplicateLocatorCode");

		Flight flight = booking.getFlight();
		boolean validFlight = flight != null && !flight.isDraftMode() && flight.getScheduledDeparture() != null && flight.getScheduledDeparture().after(MomentHelper.getCurrentMoment());
		super.state(validFlight, "flight", "customer.booking.form.error.invalidFlight");
	}

	@Override
	public void perform(final Booking booking) {
		booking.setDraftMode(true);
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;
		SelectChoices travelClassChoices = SelectChoices.from(TravelClass.class, booking.getTravelClass());
		Collection<Flight> allFlights;
		Collection<Flight> flights;

		allFlights = this.repository.findAllFlights();
		flights = allFlights.stream().filter(f -> f.getScheduledDeparture() != null && f.getScheduledDeparture().after(MomentHelper.getCurrentMoment()) && !f.isDraftMode()).collect(Collectors.toList());

		SelectChoices flightChoices = SelectChoices.from(flights, "id", booking.getFlight());

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "price", "lastNibble", "flight", "draftMode");
		dataset.put("travelClassOptions", travelClassChoices);
		dataset.put("flightsOptions", flightChoices);

		super.getResponse().addData(dataset);
	}

}
