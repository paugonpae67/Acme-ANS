
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
public class ManagerLegUpdateService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private ManagerLegRepository			repository;

	@Autowired
	private AdministratorAirportRepository	airportRepository;

	@Autowired
	private AdministratorAircraftRepository	aircraftRepository;


	@Override
	public void authorise() {
		int legId = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(legId);
		Manager manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();
		boolean status = leg != null && leg.isDraftMode() && leg.getFlight().getManager().getId() == manager.getId();
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int legId = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(legId);
		super.getBuffer().addData(leg);

		Collection<Airport> airports = this.airportRepository.findAllAirports();

		SelectChoices departureChoices = new SelectChoices();
		departureChoices.add("0", "----", leg.getDepartureAirport() == null);
		for (Airport airport : airports) {
			String iata = airport.getIataCode();
			boolean selected = leg.getDepartureAirport() != null && iata.equals(leg.getDepartureAirport().getIataCode());
			departureChoices.add(iata, iata, selected);
		}
		super.getResponse().addGlobal("departureAirports", departureChoices);

		SelectChoices arrivalChoices = new SelectChoices();
		arrivalChoices.add("0", "----", leg.getArrivalAirport() == null);
		for (Airport airport : airports) {
			String iata = airport.getIataCode();
			boolean selected = leg.getArrivalAirport() != null && iata.equals(leg.getArrivalAirport().getIataCode());
			arrivalChoices.add(iata, iata, selected);
		}
		super.getResponse().addGlobal("arrivalAirports", arrivalChoices);

		Collection<Aircraft> aircrafts = this.aircraftRepository.findAllAircrafts();
		SelectChoices aircraftChoices = new SelectChoices();
		aircraftChoices.add("0", "----", leg.getAircraft() == null);
		for (Aircraft ac : aircrafts) {
			String key = Integer.toString(ac.getId());
			String label = ac.getRegistrationNumber();
			boolean selected = leg.getAircraft() != null && key.equals(Integer.toString(leg.getAircraft().getId()));
			aircraftChoices.add(key, label, selected);
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
		} else
			leg.setAircraft(null);
	}

	@Override
	public void validate(final Leg leg) {
		super.state(leg.getScheduledDeparture().before(leg.getScheduledArrival()), "scheduledDeparture", "manager.leg.error.departureBeforeArrival");

		Leg existing = this.repository.findLegByFlightNumber(leg.getFlightNumber());
		boolean validFlightNumber = existing == null || existing.getId() == leg.getId();
		super.state(validFlightNumber, "flightNumber", "manager.leg.error.duplicateFlightNumber");
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
