
package acme.features.manager.flight;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.entities.flights.FlightIndication;
import acme.realms.Manager;

@GuiService
public class ManagerFlightUpdateService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private ManagerFlightRepository repository;


	@Override
	public void authorise() {
		boolean status = true;
		String method = super.getRequest().getMethod();
		if (method.equals("GET"))
			status = false;
		else {
			int flightId = super.getRequest().getData("id", int.class);
			Flight flight = this.repository.findById(flightId);
			Manager manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();

			status = flight != null && flight.isDraftMode() && flight.getManager().getId() == manager.getId();
		}

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.findById(id);
		super.getBuffer().addData(flight);
	}

	@Override
	public void bind(final Flight flight) {
		super.bindObject(flight, "tag", "indication", "cost", "description");
	}

	@Override
	public void validate(final Flight flight) {
		if (flight.getCost() != null && flight.getCost().getCurrency() != null) {
			String currency = flight.getCost().getCurrency().toUpperCase();
			boolean isAccepted = currency.equals("EUR") || currency.equals("GBP") || currency.equals("USD");
			super.state(isAccepted, "cost", "manager.flight.form.error.invalid-currency");
		}
	}

	@Override
	public void perform(final Flight flight) {
		this.repository.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset = super.unbindObject(flight, "tag", "indication", "cost", "description");

		if (flight.getScheduledDeparture() != null)
			dataset.put("scheduledDeparture", new Object[] {
				flight.getScheduledDeparture()
			});

		if (flight.getScheduledArrival() != null)
			dataset.put("scheduledArrival", new Object[] {
				flight.getScheduledArrival()
			});

		dataset.put("originCity", flight.getOriginCity());
		dataset.put("destinationCity", flight.getDestinationCity());
		int layovers = flight.getNumberOfLayovers();
		dataset.put("numberOfLayovers", layovers >= 0 ? layovers : 0);

		dataset.put("indications", SelectChoices.from(FlightIndication.class, flight.getIndication()));

		// 👇 Esto era lo que faltaba
		dataset.put("draftMode", flight.isDraftMode());

		super.getResponse().addData(dataset);
	}

}
