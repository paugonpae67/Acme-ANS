
package acme.features.administrator.aircraft;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.airlines.Airline;

@GuiService
public class AdministratorAircraftShowService extends AbstractGuiService<Administrator, Aircraft> {

	//Internal state -----------------------------------------------------------------------

	@Autowired
	private AdministratorAircraftRepository repository;

	//AbstractGuiService interface ---------------------------------------------------------


	@Override
	public void authorise() {

		if (!super.getRequest().getMethod().equals("GET"))
			super.getResponse().setAuthorised(false);
		else {
			boolean status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
			super.getResponse().setAuthorised(status);

			Integer aircraftId = super.getRequest().getData("id", Integer.class);
			if (aircraftId == null)
				super.getResponse().setAuthorised(false);
			else {
				Aircraft aircraft = this.repository.findAircraftById(aircraftId);
				super.getResponse().setAuthorised(aircraft != null && aircraftId == aircraft.getId());
			}

		}
	}

	@Override
	public void load() {
		Aircraft aircraft;
		int id;

		id = super.getRequest().getData("id", int.class);
		aircraft = this.repository.findAircraftById(id);

		super.getBuffer().addData(aircraft);

	}

	@Override
	public void unbind(final Aircraft aircraft) {
		Dataset dataset;

		Collection<Airline> airlines = this.repository.findAllAirlines();
		SelectChoices airlineChoices = SelectChoices.from(airlines, "iataCode", aircraft.getAirline());

		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details", "airline", "disabled");
		dataset.put("confirmation", false);
		dataset.put("airlinesOptions", airlineChoices);

		super.getResponse().addData(dataset);
	}

}
