
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
		boolean status = true;
		String method = super.getRequest().getMethod();
		if (method.equals("GET") && super.getRequest().hasData("id", int.class))
			status = false;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Manager manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();

		Flight flight = new Flight();
		flight.setTag("");
		flight.setIndication(FlightIndication.NOT_SELF_TRANSFER);
		flight.setCost(new Money());
		flight.getCost().setAmount(0.0);
		flight.getCost().setCurrency("USD");
		flight.setDescription("");
		flight.setDraftMode(true);
		flight.setManager(manager);

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
		dataset.put("indications", SelectChoices.from(FlightIndication.class, flight.getIndication()));
		super.getResponse().addData(dataset);
	}
}
