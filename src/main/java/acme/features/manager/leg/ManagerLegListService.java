
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
		int flightId = super.getRequest().getData("flightId", int.class);
		var flight = this.flightRepository.findFlightById(flightId);
		var manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();

		boolean authorised = flight != null && flight.getManager().getId() == manager.getId();
		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int flightId = super.getRequest().getData("flightId", int.class);
		boolean flightDraftMode = this.flightRepository.findFlightById(flightId).isDraftMode();
		Collection<Leg> legs = this.repository.findLegsByflightIdOrderByMoment(flightId);
		super.getBuffer().addData(legs);
		super.getResponse().addGlobal("flightDraftMode", flightDraftMode);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status");
		dataset.put("originCity", leg.getDepartureAirport().getCity());
		dataset.put("destinationCity", leg.getArrivalAirport().getCity());
		dataset.put("flightId", super.getRequest().getData("flightId", int.class));
		super.getResponse().addData(dataset);
	}
}
