
package acme.features.manager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.legs.Leg;
import acme.features.manager.flight.ManagerFlightRepository;
import acme.realms.Manager;

@GuiService
public class ManagerLegListService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private ManagerLegRepository	repository;

	@Autowired
	private ManagerFlightRepository	flightRepository;


	@Override
	public void authorise() {
		// All managers are authorised to list the legs of a flight.
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		// Retrieve the masterId from the request parameters
		int flightId = super.getRequest().getData("flightId", int.class);
		boolean flightDraftMode = this.flightRepository.findFlightById(flightId).isDraftMode();

		// Retrieve the legs associated with the given flight, ordered by scheduledDeparture
		Collection<Leg> legs = this.repository.findLegsByflightIdOrderByMoment(flightId);
		super.getBuffer().addData(legs);
		// Add the flightDraftMode flag to the response so the JSP can access it.
		super.getResponse().addGlobal("flightDraftMode", flightDraftMode);
	}

	@Override
	public void unbind(final Leg leg) {
		// Unbind key fields from Leg.
		Dataset dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status");
		// Add computed origin and destination cities from the associated Airports.
		dataset.put("originCity", leg.getDepartureAirport().getCity());
		dataset.put("destinationCity", leg.getArrivalAirport().getCity());
		dataset.put("flightId", super.getRequest().getData("flightId", int.class));
		super.getResponse().addData(dataset);
	}

}
