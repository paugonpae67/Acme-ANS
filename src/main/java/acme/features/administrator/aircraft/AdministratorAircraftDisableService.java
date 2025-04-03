
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
import acme.entities.legs.Leg;

@GuiService
public class AdministratorAircraftDisableService extends AbstractGuiService<Administrator, Aircraft> {

	@Autowired
	private AdministratorAircraftRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Aircraft aircraft = this.repository.findAircraftById(id);

		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {
		super.bindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details", "airline");
	}

	@Override
	public void validate(final Aircraft aircraft) {
		boolean confirmation;
		Collection<Leg> activeAndUpcomingLegsAssociated;

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

		activeAndUpcomingLegsAssociated = this.repository.findActiveAndUpcomingLegsByAircraftId(aircraft.getId());
		if (!activeAndUpcomingLegsAssociated.isEmpty())
			super.state(false, "confirmation", "acme.validation.leg.message");

	}

	@Override
	public void perform(final Aircraft aircraft) {

		if (aircraft.getStatus().equals(AircraftStatus.ACTIVE_SERVICE)) {
			aircraft.setStatus(AircraftStatus.UNDER_MAINTENANCE);
			aircraft.setDisabled(true);
		}

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
