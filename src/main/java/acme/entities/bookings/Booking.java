
package acme.entities.bookings;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidBooking;
import acme.entities.flights.Flight;
import acme.realms.Customer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@ValidBooking
/*
 * @Table(indexes = {
 * 
 * @Index(columnList = "customer_id"), @Index(columnList = "locatorCode")
 * })
 */
public class Booking extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(pattern = "^[A-Z0-9]{6,8}$")
	@Column(unique = true)
	private String				locatorCode;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				purchaseMoment;

	@Mandatory
	@Valid
	@Automapped
	private TravelClass			travelClass;

	@Optional
	@ValidString(min = 4, max = 4, pattern = "[0-9]{4}", message = "{acme.validation.lastNibble.notPattern.message}")
	@Automapped
	private String				lastNibble;

	@Mandatory
	@Automapped
	private boolean				draftMode;

	//Derived attributes  ---------------------------------------------------


	@Transient
	public Money getPrice() {
		Money res = new Money();
		Flight flight = this.getFlight();

		BookingRecordRepository bookingRecordRepository = SpringHelper.getBean(BookingRecordRepository.class);
		Integer passengersNumber = bookingRecordRepository.countPassengersByBooking(this.getId());

		if (flight != null) {
			Money flightCost = flight.getCost();
			res.setAmount(flightCost.getAmount() * passengersNumber);
			res.setCurrency(flightCost.getCurrency());
		} else {
			res.setAmount(0.00);
			res.setCurrency("EUR");
		}
		return res;
	}

	//Relationships ---------------------------------------------------------


	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Flight		flight;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Customer	customer;

}
