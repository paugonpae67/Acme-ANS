
package acme.features.manager.flight;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.entities.flights.FlightIndication;
import acme.realms.Manager;

@GuiService
public class ManagerFlightCreateService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private ManagerFlightRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		// Retrieve the current manager from the active realm.
		Manager manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();

		// Create a new flight and set default values.
		Flight flight = new Flight();
		flight.setTag("");
		flight.setIndication(FlightIndication.NOT_SELF_TRANSFER);
		// Create a default Money value, e.g. 0.0 USD (adjust as needed)
		flight.setCost(new Money());
		flight.getCost().setAmount(0.0);
		flight.getCost().setCurrency("USD");
		flight.setDescription("");
		flight.setDraftMode(true);
		// Associate the flight with the current manager.
		flight.setManager(manager);

		// Add the new flight to the buffer.
		super.getBuffer().addData(flight);
	}

	@Override
	public void bind(final Flight flight) {
		super.bindObject(flight, "tag", "indication", "cost", "description");
	}

	@Override
	public void validate(final Flight flight) {
		// Add any flight-specific validation here, e.g., check that required fields are not blank.
		// For now, we leave it empty.
	}

	@Override
	public void perform(final Flight flight) {

		this.repository.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset = super.unbindObject(flight, "tag", "indication", "cost", "description");
		dataset.put("indications", SelectChoices.from(FlightIndication.class, flight.getIndication()));
		super.getResponse().addData(dataset);
	}

}
