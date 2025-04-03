
package acme.features.administrator.airport;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airports.Airport;
import acme.entities.airports.OperationalType;

@GuiService
public class AdministratorAirportUpdateService extends AbstractGuiService<Administrator, Airport> {

	@Autowired
	private AdministratorAirportRepository airportRepository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Airport airport;
		int id;

		id = super.getRequest().getData("id", int.class);
		airport = this.airportRepository.findAirportById(id);

		super.getBuffer().addData(airport);
	}

	@Override
	public void bind(final Airport airport) {
		super.bindObject(airport, "name", "iataCode", "city", "country", "operationalScope", "website", "email", "phoneNumber");
	}

	@Override
	public void validate(final Airport airport) {
		Airport existAirport = this.airportRepository.findAirportByIataCode(airport.getIataCode());
		boolean valid = existAirport == null || existAirport.getId() == airport.getId();
		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
		super.state(valid, "iataCode", "acme.validation.airport.form.error.duplicateIata");

	}

	@Override
	public void perform(final Airport airport) {
		this.airportRepository.save(airport);
	}

	@Override
	public void unbind(final Airport airport) {
		Dataset dataset;
		SelectChoices choices;
		choices = SelectChoices.from(OperationalType.class, airport.getOperationalScope());
		dataset = super.unbindObject(airport, "name", "iataCode", "city", "country", "operationalScope", "website", "email", "phoneNumber");
		dataset.put("scope", choices.getSelected().getKey());
		dataset.put("scopes", choices);
		super.getResponse().addData(dataset);
	}
}
