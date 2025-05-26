
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
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
		// Solo permitir método POST para eliminar (DELETE se simula comúnmente con POST)
		String method = super.getRequest().getMethod();
		if (!"POST".equalsIgnoreCase(method)) {
			super.getResponse().setAuthorised(false);
			return;
		}

		// Verificar existencia, estado y propiedad del vuelo
		int flightId = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.findById(flightId);
		int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		boolean status = flight != null && flight.isDraftMode() && flight.getManager().getId() == managerId;

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
	}
}
