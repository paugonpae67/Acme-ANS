
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
public class AdministratorAircraftUpdateService extends AbstractGuiService<Administrator, Aircraft> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAircraftRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = true;

		status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		super.getResponse().setAuthorised(status);

		if (!super.getRequest().getMethod().equals("POST")) {
			super.getResponse().setAuthorised(false);
			return;
		}

		if (super.getRequest().hasData("id", Integer.class)) {
			Integer aircraftId = super.getRequest().getData("id", Integer.class);
			if (aircraftId == null)
				status = false;
			else {
				Aircraft aircraft = this.repository.findAircraftById(aircraftId);
				if (aircraft == null || aircraft.isDisabled())
					status = false;
				else {
					Integer airlineId = super.getRequest().getData("airline", Integer.class);
					if (airlineId == null)
						status = false;
					else if (airlineId != 0) {
						Airline airline = this.repository.findAirlineById(airlineId);
						status = status && airline != null;
					}
				}
			}
		}
		super.getResponse().setAuthorised(status);
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
	public void bind(final Aircraft aircraft) {
		super.bindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "details", "airline");
	}

	@Override
	public void validate(final Aircraft aircraft) {
		boolean confirmation;

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

		Airline airline = aircraft.getAirline();
		boolean validAirline = airline != null;
		super.state(validAirline, "airline", "administrator.aircraft.form.error.invalidAirline");
	}

	@Override
	public void perform(final Aircraft aircraft) {
		this.repository.save(aircraft);
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
