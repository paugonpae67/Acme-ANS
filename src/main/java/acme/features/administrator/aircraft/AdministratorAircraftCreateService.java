
package acme.features.administrator.aircraft;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.AircraftStatus;
import acme.entities.airlines.Airline;

@GuiService
public class AdministratorAircraftCreateService extends AbstractGuiService<Administrator, Aircraft> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAircraftRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status1 = true;

		status1 = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		if (!super.getRequest().getMethod().equals("POST") && super.getRequest().hasData("id", int.class))
			status1 = false;

		boolean status2 = true;
		if (super.getRequest().hasData("flight", Integer.class)) {
			Integer airlineId = super.getRequest().getData("airline", Integer.class);
			if (airlineId == null)
				status2 = false;
			else if (airlineId != 0) {
				Airline airline = this.repository.findAirlineById(airlineId);
				status2 = airline != null;
			}
		}
		super.getResponse().setAuthorised(status1 && status2);

	}

	@Override
	public void load() {
		Aircraft aircraft = new Aircraft();
		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {
		super.bindObject(aircraft, "model", "registrationNumber", "capacity", "status", "cargoWeight", "details", "airline");
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
		if (aircraft.getStatus().equals(AircraftStatus.ACTIVE_SERVICE))
			aircraft.setDisabled(false);
		else if (aircraft.getStatus().equals(AircraftStatus.UNDER_MAINTENANCE))
			aircraft.setDisabled(true);
		this.repository.save(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		SelectChoices statusChoices;
		Dataset dataset;

		Collection<Airline> airlines = this.repository.findAllAirlines();
		SelectChoices airlineChoices = SelectChoices.from(airlines, "iataCode", aircraft.getAirline());

		statusChoices = SelectChoices.from(AircraftStatus.class, aircraft.getStatus());

		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details", "airline", "disabled");
		dataset.put("confirmation", false);
		dataset.put("statusOptions", statusChoices);
		dataset.put("airlinesOptions", airlineChoices);

		super.getResponse().addData(dataset);
	}

}
