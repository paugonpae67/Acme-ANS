
package acme.features.administrator.airline;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.services.AbstractService;
import acme.client.services.GuiService;
import acme.entities.airlines.Airline;
import acme.entities.airlines.AirlineType;
import acme.entities.airports.Airport;

@GuiService
public class AdministratorAirlineUpdateService extends AbstractService<Administrator, Airline> {

	@Autowired
	protected AdministratorAirlineRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Airline airline = this.repository.findOneAirlineById(id);
		super.getBuffer().addData(airline);
	}

	@Override
	public void bind(final Airline airline) {
		int airportId = super.getRequest().getData("airport", int.class);
		Airport airport = this.repository.findOneAirportById(airportId);
		airline.setAirport(airport);

		super.bindObject(airline, "name", "iataCode", "website", "type", "foundationMoment", "email", "phoneNumber");
	}

	@Override
	public void validate(final Airline airline) {

	}

	@Override
	public void perform(final Airline airline) {
		this.repository.save(airline);
	}

	@Override
	public void unbind(final Airline airline) {
		Collection<Airport> airports = this.repository.findAllAirports();
		Collection<AirlineType> types = Arrays.asList(AirlineType.values());

		super.getResponse().addData("airports", airports);
		super.getResponse().addData("types", types);

		super.getResponse().addData(super.unbindObject(airline, "name", "iataCode", "website", "type", "foundationMoment", "email", "phoneNumber", "airport"));
	}
}
