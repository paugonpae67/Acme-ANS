
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
public class ManagerFlightDeleteService extends AbstractGuiService<Manager, Flight> {

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
			int flightId;
			Flight flight;

			flightId = super.getRequest().getData("id", int.class);
			flight = this.repository.findById(flightId);

			int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();

			status = flight != null && flight.isDraftMode() && flight.getManager().getId() == managerId;
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.findById(id);
		super.getBuffer().addData(flight);
	}

	@Override
	public void bind(final Flight flight) {
		super.bindObject(flight, "tag", "indication", "cost", "description");
	}

	@Override
	public void validate(final Flight flight) {
		Collection<Leg> legs = this.managerLegRepository.findLegsByflightId(flight.getId());

		boolean allLegsInDraft = legs.stream().allMatch(Leg::isDraftMode);

		super.state(allLegsInDraft, "*", "manager.flight.form.error.published-leg");
	}

	@Transactional
	@Override
	public void perform(final Flight flight) {
		Integer flightId = flight.getId();

		Collection<Leg> legs = this.managerLegRepository.findLegsByflightId(flightId);
		if (legs == null)
			throw new IllegalStateException("managerLegRepository.findLegsByflightId(" + flightId + ") returned NULL");

		for (Leg leg : legs) {
			// Borrar logs relacionados a FlightAssignments del Leg
			this.managerLegRepository.deleteActivityLogsByLegId(leg.getId());

			// Borrar logs relacionados a Claims del Leg
			this.managerLegRepository.deleteTrackingLogsByLegId(leg.getId());

			// Borrar Claims del Leg
			this.managerLegRepository.deleteClaimsByLegId(leg.getId());

			// Borrar FlightAssignments del Leg
			this.managerLegRepository.deleteAssignmentsByLegId(leg.getId());

			// Finalmente borrar el Leg
			this.managerLegRepository.delete(leg);
		}

		// Finalmente borrar el vuelo
		this.repository.deleteById(flightId);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset = super.unbindObject(flight, "tag", "indication", "cost", "description");

		dataset.put("scheduledDeparture", flight.getScheduledDeparture());
		dataset.put("scheduledArrival", flight.getScheduledArrival());
		dataset.put("originCity", flight.getOriginCity());
		dataset.put("destinationCity", flight.getDestinationCity());
		int layovers = flight.getNumberOfLayovers();
		dataset.put("numberOfLayovers", layovers >= 0 ? layovers : 0);

		dataset.put("draftMode", flight.isDraftMode());
		dataset.put("indications", SelectChoices.from(FlightIndication.class, flight.getIndication()));

		super.getResponse().addData(dataset);
	}

}
