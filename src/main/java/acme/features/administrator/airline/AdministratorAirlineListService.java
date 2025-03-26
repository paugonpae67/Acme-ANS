
package acme.features.administrator.airline;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.services.AbstractService;
import acme.client.services.GuiService;
import acme.entities.airlines.Airline;

@GuiService
public class AdministratorAirlineListService extends AbstractService<Administrator, Airline> {

	@Autowired
	protected AdministratorAirlineRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Airline> airlines = this.repository.findAllAirlines();
		super.getBuffer().addData(airlines);
	}

	@Override
	public void unbind(final Airline airline) {
		assert airline != null;

		super.getResponse().addData(super.unbindObject(airline, "name", "iataCode", "website", "type", "foundationMoment", "email", "phoneNumber", "airport.name"));
	}
}
