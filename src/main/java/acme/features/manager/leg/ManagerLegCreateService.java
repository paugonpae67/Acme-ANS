
package acme.features.manager.leg;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.airports.Airport;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.features.administrator.aircraft.AdministratorAircraftRepository;
import acme.features.administrator.airport.AdministratorAirportRepository;
import acme.features.manager.flight.ManagerFlightRepository;
import acme.realms.Manager;

@GuiService
public class ManagerLegCreateService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private ManagerLegRepository			repository;

	@Autowired
	private ManagerFlightRepository			flightRepository;

	@Autowired
	private AdministratorAirportRepository	airportRepository;

	@Autowired
	private AdministratorAircraftRepository	aircraftRepository;


	@Override
	public void authorise() {
		// Verificar método HTTP: permitir GET (mostrar formulario) y POST (procesar)
		String method = super.getRequest().getMethod();
		boolean allowedMethod = "GET".equalsIgnoreCase(method) || "POST".equalsIgnoreCase(method);

		// Obtener el parámetro "flightId"
		Integer flightId = super.getRequest().getData("flightId", Integer.class);
		if (flightId == null || !allowedMethod) {
			super.getResponse().setAuthorised(false);
			return;
		}

		// Verificar propiedad del vuelo y que esté en modo borrador
		Flight flight = this.flightRepository.findFlightById(flightId);
		Manager manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();

		boolean status = flight != null && flight.isDraftMode() && flight.getManager().getId() == manager.getId();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Integer flightId = super.getRequest().getData("flightId", Integer.class);
		Flight flight = this.flightRepository.findFlightById(flightId);

		Leg leg = new Leg();
		leg.setFlight(flight);
		leg.setDraftMode(true);
		leg.setFlightNumber("");
		leg.setScheduledDeparture(MomentHelper.getCurrentMoment());
		leg.setScheduledArrival(new Date(MomentHelper.getCurrentMoment().getTime() + 60000));
		leg.setStatus(LegStatus.ON_TIME);

		Collection<Airport> airports = this.airportRepository.findAllAirports();
		SelectChoices departureChoices = new SelectChoices();
		departureChoices.add("0", "----", true);
		for (Airport airport : airports) {
			String iata = airport.getIataCode();
			departureChoices.add(iata, iata, false);
		}
		super.getResponse().addGlobal("departureAirports", departureChoices);

		SelectChoices arrivalChoices = new SelectChoices();
		arrivalChoices.add("0", "----", true);
		for (Airport airport : airports) {
			String iata = airport.getIataCode();
			arrivalChoices.add(iata, iata, false);
		}
		super.getResponse().addGlobal("arrivalAirports", arrivalChoices);

		Collection<Aircraft> aircrafts = this.aircraftRepository.findAllAircrafts();
		SelectChoices aircraftChoices = new SelectChoices();
		aircraftChoices.add("0", "----", true);
		for (Aircraft ac : aircrafts) {
			String key = Integer.toString(ac.getId());
			String label = ac.getRegistrationNumber();
			aircraftChoices.add(key, label, false);
		}
		super.getResponse().addGlobal("aircraftChoices", aircraftChoices);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		super.bindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status");

		String statusStr = super.getRequest().getData("status", String.class);
		if (statusStr != null && !statusStr.isEmpty())
			try {
				LegStatus newStatus = LegStatus.valueOf(statusStr);
				leg.setStatus(newStatus);
			} catch (IllegalArgumentException ex) {
			}

		String departureIata = super.getRequest().getData("departureAirport", String.class);
		String arrivalIata = super.getRequest().getData("arrivalAirport", String.class);
		Airport departureAirport = this.airportRepository.findAirportByIataCode(departureIata);
		Airport arrivalAirport = this.airportRepository.findAirportByIataCode(arrivalIata);
		leg.setDepartureAirport(departureAirport);
		leg.setArrivalAirport(arrivalAirport);

		Integer aircraftId = super.getRequest().getData("aircraft", Integer.class);
		if (aircraftId != null && aircraftId != 0) {
			Aircraft aircraft = this.aircraftRepository.findAircraftById(aircraftId);
			leg.setAircraft(aircraft);
		}
	}

	@Override
	public void validate(final Leg leg) {
		if (leg.getDepartureAirport() != null && leg.getArrivalAirport() != null) {
			boolean valid = !(leg.getDepartureAirport().getId() == leg.getArrivalAirport().getId());
			super.state(valid, "arrivalAirport", "manager.leg.error.sameAirport");

			Leg existing = this.repository.findLegByFlightNumber(leg.getFlightNumber());
			boolean validFlightNumber = existing == null || existing.getId() == leg.getId();
			super.state(validFlightNumber, "flightNumber", "manager.leg.error.duplicateFlightNumber");
		}
	}

	@Override
	public void perform(final Leg leg) {
		this.repository.save(leg);
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
