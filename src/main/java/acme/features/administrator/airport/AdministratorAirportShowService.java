
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
public class AdministratorAirportShowService extends AbstractGuiService<Administrator, Airport> {

	@Autowired
	private AdministratorAirportRepository airportRepository;


	@Override
	public void authorise() {

		boolean status;
		int masterId = super.getRequest().getData("id", int.class);
		Airport airport = this.airportRepository.findAirportById(masterId);
		if (airport != null)
			status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		else
			status = false;
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
	public void unbind(final Airport airport) {
		Dataset dataset;
		SelectChoices choices;

		choices = SelectChoices.from(OperationalType.class, airport.getOperationalScope());
		dataset = super.unbindObject(airport, "name", "iataCode", "city", "country", "operationalScope", "website", "email", "phoneNumber");
		dataset.put("scopes", choices);
		super.getResponse().addData(dataset);
	}

}
