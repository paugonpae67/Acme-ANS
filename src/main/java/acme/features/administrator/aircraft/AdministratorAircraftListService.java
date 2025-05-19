
package acme.features.administrator.aircraft;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;

@GuiService
public class AdministratorAircraftListService extends AbstractGuiService<Administrator, Aircraft> {

	//Internal state -----------------------------------------------------------------------

	@Autowired
	private AdministratorAircraftRepository repository;

	//AbstractGuiService interface ---------------------------------------------------------


	@Override
	public void authorise() {
		try {
			boolean status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
			super.getResponse().setAuthorised(status);

			if (!super.getRequest().getMethod().equals("GET"))
				super.getResponse().setAuthorised(false);
			else if (super.getRequest().getMethod().equals("GET") && super.getRequest().hasData("id", int.class))
				super.getResponse().setAuthorised(false);
		} catch (Throwable t) {
			super.getResponse().setAuthorised(false);
		}
	}

	@Override
	public void load() {
		Collection<Aircraft> aircrafts;
		aircrafts = this.repository.findAllAircrafts();
		super.getBuffer().addData(aircrafts);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		Dataset dataset;

		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details", "airline", "disabled");
		dataset.put("airline", aircraft.getAirline().getIataCode());

		super.getResponse().addData(dataset);

	}

}
