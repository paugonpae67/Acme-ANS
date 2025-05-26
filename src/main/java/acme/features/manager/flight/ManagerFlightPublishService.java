
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.entities.flights.FlightIndication;
import acme.entities.legs.Leg;
import acme.features.manager.leg.ManagerLegRepository;
import acme.realms.Manager;

@GuiService
public class ManagerFlightPublishService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private ManagerFlightRepository	repository;

	@Autowired
	private ManagerLegRepository	managerLegRepository;


	@Override
	public void authorise() {
		boolean status = true;
		String method = super.getRequest().getMethod();
		if (method.equals("GET"))
			status = false;
		else {
			int flightId = super.getRequest().getData("id", int.class);
			Flight flight = this.repository.findById(flightId);
			Manager manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();

			status = flight != null && flight.isDraftMode() && flight.getManager().getId() == manager.getId();
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.findFlightById(id);
		super.getBuffer().addData(flight);
	}

	@Override
	public void bind(final Flight flight) {
	}

	@Override
	public void validate(final Flight flight) {
		Collection<Leg> legs = this.managerLegRepository.findLegsByflightId(flight.getId());
		boolean hasLeg = !legs.isEmpty();
		super.state(hasLeg, "*", "flight.publish.error.noLegs");
		if (hasLeg) {
			boolean allPublished = legs.stream().allMatch(leg -> !leg.isDraftMode());
			super.state(allPublished, "*", "flight.publish.error.unpublishedLegs");
		}
	}

	@Override
	public void perform(final Flight flight) {
		flight.setDraftMode(false);
		this.repository.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset = super.unbindObject(flight, "tag", "indication", "cost", "description", "draftMode");
		dataset.put("scheduledDeparture", flight.getScheduledDeparture());
		dataset.put("scheduledArrival", flight.getScheduledArrival());
		dataset.put("originCity", flight.getOriginCity());
		dataset.put("destinationCity", flight.getDestinationCity());
		dataset.put("numberOfLayovers", flight.getNumberOfLayovers());
		dataset.put("indications", SelectChoices.from(FlightIndication.class, flight.getIndication()));
		super.getResponse().addData(dataset);
	}
}
