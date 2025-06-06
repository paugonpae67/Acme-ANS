
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
public class CustomerBookingCreateService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		boolean status1 = true;

		status1 = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		if (!super.getRequest().getMethod().equals("POST") && super.getRequest().hasData("id", int.class))
			status1 = false;

		if (super.getRequest().getMethod().equals("POST")) {
			int id = super.getRequest().getData("id", int.class);
			status1 = id == 0;
		}

		boolean status2 = true;
		if (super.getRequest().hasData("flight", Integer.class)) {
			Integer flightId = super.getRequest().getData("flight", Integer.class);
			if (flightId == null)
				status2 = false;
			else if (flightId != 0) {
				Flight flight = this.repository.findFlightById(flightId);
				status2 = flight != null && !flight.isDraftMode() && flight.getScheduledDeparture() != null && flight.getScheduledDeparture().after(MomentHelper.getCurrentMoment());
			}
		}

		super.getResponse().setAuthorised(status1 && status2);

	}

	@Override
	public void load() {
		Customer customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();
		Booking booking = new Booking();

		booking.setPurchaseMoment(MomentHelper.getCurrentMoment());
		booking.setCustomer(customer);
		booking.setDraftMode(true);

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
		if (flight != null) {
			boolean validFlight = !flight.isDraftMode() && flight.getScheduledDeparture() != null && flight.getScheduledDeparture().after(MomentHelper.getCurrentMoment());
			super.state(validFlight, "flight", "customer.booking.form.error.invalidFlight");
		}
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
		flights = allFlights.stream().filter(f -> f.getScheduledDeparture() != null && f.getScheduledDeparture().after(MomentHelper.getCurrentMoment()) && f.isDraftMode() == false).collect(Collectors.toList());

		SelectChoices flightChoices = SelectChoices.from(flights, "id", booking.getFlight());

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "price", "lastNibble", "flight", "draftMode");
		dataset.put("travelClassOptions", travelClassChoices);
		dataset.put("flightsOptions", flightChoices);

		super.getResponse().addData(dataset);

	}

}
