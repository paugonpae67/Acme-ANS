
package acme.features.manager.leg;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.legs.Leg;
import acme.realms.Manager;

@GuiService
public class ManagerLegPublishService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private ManagerLegRepository repository;


	@Override
	public void authorise() {
		// Verificar el método HTTP (sólo permitir POST)
		String method = super.getRequest().getMethod();
		if (!"POST".equalsIgnoreCase(method)) {
			super.getResponse().setAuthorised(false);
			return;
		}

		// Obtener y validar los datos del leg
		int legId = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(legId);

		// Obtener el manager autenticado
		Manager manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();

		// Verificar que el leg esté en modo borrador y pertenezca al manager
		boolean status = leg != null && leg.isDraftMode() && leg.getFlight().getManager().getId() == manager.getId();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(id);
		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		super.bindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival");
	}

	@Override
	public void validate(final Leg leg) {
	}

	@Override
	public void perform(final Leg leg) {
		leg.setDraftMode(false);
		this.repository.save(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "draftMode");
		super.getResponse().addData(dataset);
	}
}
