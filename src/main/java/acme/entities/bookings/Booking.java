
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
import acme.entities.flights.Flight;
import acme.realms.Customer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
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
	@ValidString(min = 4, max = 4)
	@Automapped
	private String				lastNibble;

	//Derived attributes  ---------------------------------------------------


	@Transient
	private Money getPrice() {
		BookingRecordRepository bookingRecordRepository = SpringHelper.getBean(BookingRecordRepository.class);
		Integer passengersNumber = bookingRecordRepository.countPassengersByBooking(this.getId());

		if (passengersNumber != null && this.flight != null) {
			double totalCost = this.flight.getCost().getAmount() * passengersNumber;
			Money money = new Money();
			money.setAmount(totalCost);
			money.setCurrency(this.flight.getCost().getCurrency());
			return money;
		} else {
			Money money = new Money();
			money.setAmount(0.0);
			money.setCurrency(this.flight.getCost().getCurrency());
			return money;
		}
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
