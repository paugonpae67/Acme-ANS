
package acme.features.manager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.airports.Airport;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.features.administrator.aircraft.AdministratorAircraftRepository;
import acme.features.administrator.airport.AdministratorAirportRepository;
import acme.realms.Manager;

@GuiService
public class ManagerLegShowService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private ManagerLegRepository			repository;

	@Autowired
	private AdministratorAircraftRepository	aircraftRepository;

	@Autowired
	private AdministratorAirportRepository	airportRepository;


	@Override
	public void authorise() {
		// Solo permitir método GET
		String method = super.getRequest().getMethod();
		if (!"GET".equalsIgnoreCase(method)) {
			super.getResponse().setAuthorised(false);
			return;
		}

		int legId = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(legId);
		boolean authorised = false;

		if (leg != null && leg.getFlight() != null && leg.getFlight().getManager() != null) {

			int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
			authorised = leg.getFlight().getManager().getId() == managerId;
		}

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(id);
		super.getBuffer().addData(leg);

		Collection<Airport> airports = this.airportRepository.findAllAirports();
		SelectChoices departureChoices = new SelectChoices();
		departureChoices.add("0", "----", leg.getDepartureAirport() == null);
		for (Airport airport : airports) {
			String iata = airport.getIataCode();
			boolean isSelected = leg.getDepartureAirport() != null && iata.equals(leg.getDepartureAirport().getIataCode());
			departureChoices.add(iata, iata, isSelected);
		}

		SelectChoices arrivalChoices = new SelectChoices();
		arrivalChoices.add("0", "----", leg.getArrivalAirport() == null);
		for (Airport airport : airports) {
			String iata = airport.getIataCode();
			boolean isSelected = leg.getArrivalAirport() != null && iata.equals(leg.getArrivalAirport().getIataCode());
			arrivalChoices.add(iata, iata, isSelected);
		}

		Collection<Aircraft> aircrafts = this.aircraftRepository.findAllAircrafts();
		SelectChoices aircraftChoices = new SelectChoices();
		aircraftChoices.add("0", "----", leg.getAircraft() == null);
		for (Aircraft ac : aircrafts) {
			String key = Integer.toString(ac.getId());
			String label = ac.getRegistrationNumber();
			boolean isSelected = leg.getAircraft() != null && key.equals(Integer.toString(leg.getAircraft().getId()));
			aircraftChoices.add(key, label, isSelected);
		}

		super.getResponse().addGlobal("departureAirports", departureChoices);
		super.getResponse().addGlobal("arrivalAirports", arrivalChoices);
		super.getResponse().addGlobal("aircraftChoices", aircraftChoices);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "draftMode");

		if (leg.getDepartureAirport() != null) {
			dataset.put("departureAirport", leg.getDepartureAirport().getIataCode());
			dataset.put("originCity", leg.getDepartureAirport().getCity());
		}
		if (leg.getArrivalAirport() != null) {
			dataset.put("arrivalAirport", leg.getArrivalAirport().getIataCode());
			dataset.put("destinationCity", leg.getArrivalAirport().getCity());
		}
		if (leg.getAircraft() != null) {
			dataset.put("aircraft", leg.getAircraft().getId());
			dataset.put("aircraftRegistration", leg.getAircraft().getRegistrationNumber());
		}

		dataset.put("scheduledDeparture", new Object[] {
			leg.getScheduledDeparture()
		});
		dataset.put("scheduledArrival", new Object[] {
			leg.getScheduledArrival()
		});
		dataset.put("status", leg.getStatus());
		dataset.put("legStatuses", SelectChoices.from(LegStatus.class, leg.getStatus()));
		dataset.put("flightId", leg.getFlight().getId());
		dataset.put("draftMode", leg.isDraftMode());

		super.getResponse().addData(dataset);
	}
}
