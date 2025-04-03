
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
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
		boolean status;
		int flightId;
		Flight flight;

		flightId = super.getRequest().getData("id", int.class);
		flight = this.repository.findById(flightId);

		// Retrieve the current manager from the active realm.
		int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		// Allow deletion only if:
		// - The flight exists,
		// - The flight is in draft mode (i.e. not published),
		// - And the flight belongs to the current manager.
		status = flight != null && flight.isDraftMode() && flight.getManager().getId() == managerId;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Flight flight;
		int id = super.getRequest().getData("id", int.class);
		flight = this.repository.findById(id);
		super.getBuffer().addData(flight);
	}

	@Override
	public void bind(final Flight flight) {
		// Bind the fields; for deletion, this may not be strictly necessary, but we follow the template.
		super.bindObject(flight, "tag", "indication", "cost", "description");
	}

	@Override
	public void validate(final Flight flight) {
		// No additional validation is needed for deletion if the authorisation is correct.
	}

	@Override
	public void perform(final Flight flight) {
		// Retrieve all legs associated with this flight
		Collection<Leg> legs = this.managerLegRepository.findLegsByflightId(flight.getId());
		// Delete each leg first to avoid foreign key constraint violations
		for (Leg leg : legs)
			this.managerLegRepository.delete(leg);
		// Now delete the flight itself
		this.repository.delete(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;
		dataset = super.unbindObject(flight, "tag", "indication", "cost", "description");
		// Optionally add the draftMode flag for informational purposes.
		dataset.put("draftMode", flight.isDraftMode());
		super.getResponse().addData(dataset);
	}
}
